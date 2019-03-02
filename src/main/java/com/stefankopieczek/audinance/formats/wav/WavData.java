package com.stefankopieczek.audinance.formats.wav;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.*;

import java.io.*;
import java.util.logging.Logger;

/**
 * Represents a WAV audio clip, undecoded, backed by data drawn from some
 * abstract source.
 */
public class WavData extends EncodedAudio
{
	private static final Logger sLogger = Logger.getLogger(WavData.class.getName());

    /**
	 * The format of the WAV data. This includes recording information,
	 * as the sample rate, but also datatype-specific parameters such as
	 * the encoding type (PCM, ALAW, ...)
	 */
	private WavFormat mFormat;
	
	/**
	 * Constructs a WAV clip backed by the given .wav file.
	 * 
	 * @param file The wav file to build this clip from.
	 * @throws FileNotFoundException if the specified wav file does not exist.
	 * @throws IOException if we fail to read from the file.
	 * @throws InvalidAudioFormatException TODO SMK
	 */
	public WavData(File file)
		throws FileNotFoundException, 
		       IOException,
			   InvalidAudioFormatException
	{
		super(file);
        sLogger.info("Constructed WavData object for file " + file);
	}
	
	/**
	 * Constructs a WAV clip backed by the given input stream.
	 *
	 * @param is The input stream to build this clip from.
	 * @throws IOException if we fail to read from the stream.
	 * @throws InvalidAudioFormatException TODO SMK
	 */
	public WavData(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		super(is);
        sLogger.info("Constructed WavData object for InputStream " + is);
	}
	
	/**
	 * Constructs a WAV clip from a clip of encoded audio.
	 * This will usually involve first decoding the clip, and then
	 * re-encoding it in WAV format.
	 * Recoding may happen up-front or 'just in time'.
	 *
	 * @param encodedAudio The audio on which to base this WAV clip.
	 * @param format The recording format in which to store the resultant
	 *               wav data.
	 * @throws InvalidAudioFormatException If the specified audio format is underdetermined.
	 * @throws UnsupportedFormatException SMK todo
	 */
	public WavData(EncodedAudio encodedAudio, AudioFormat format) 
		throws InvalidAudioFormatException, 
		       UnsupportedFormatException
	{
		super(encodedAudio, format);
        sLogger.info("Constructed WavData object from audio " + encodedAudio + " with target format " + format);
	}
	
	/**
	 * Takes the given <tt>EncodedSource</tt> containing valid WAV data
	 * and a format object describing how the data is to be interpreted,
	 * and builds a new <tt>WavData</tt> object.
	 *
	 * @param wavSource A source of valid WAV data.
	 * @param format The recording format of the data provided.
	 */
	public WavData(EncodedSource wavSource, WavFormat format)
	{		
		mData = wavSource;
		mFormat = format;
        sLogger.info("Constructed WavData object from existing WAV source " + wavSource + " with target format " +
                     format);
	}

	@Override
	public DecodedAudio getDecodedAudio() 
        throws InvalidWavDataException, UnsupportedWavEncodingException 
	{
	    sLogger.info("Decoding " + this);
		WavDecoder wavDecoder = new WavDecoder(getSource());
		return wavDecoder.getDecodedAudio();
	}

	@Override
	public DataType getDataType()
	{
		return DataType.WAV;
	}
	
	@Override
	public WavFormat getFormat()
		throws UnsupportedWavEncodingException, InvalidWavDataException
	{
		if (mFormat == null)
		{
			WavDecoder wavDecoder = new WavDecoder(getSource());
			mFormat = wavDecoder.getFormat();
		}
		
		return mFormat;
	}
	
	@Override
	public void buildFromAudio(EncodedAudio encodedAudio,
			                   AudioFormat format) 
        throws InvalidAudioFormatException, UnsupportedFormatException
	{
		if (format instanceof WavFormat)
		{
			// Use the WAV format instructions if provided.
			// We can't do this with inheritance as I'm rubbish. TODO.
			buildFromAudio(encodedAudio.getDecodedAudio(), (WavFormat)format);
		}
		else
		{
			buildFromAudio(encodedAudio,
					       new WavFormat(format.getSampleRate(),
					                     format.getNumChannels(),
					                     WavEncoder.DEFAULT_ENCODING,
					                     WavEncoder.DEFAULT_BIT_DEPTH));
		}
	}
	
	private void buildFromAudio(DecodedAudio rawAudioData,
	                            WavFormat format)
		throws InvalidAudioFormatException
	{
		AudioFormat encodedFormat = rawAudioData.getFormat();
		
		// For each format parameter, if the given format does not explicitly
		// state the desired value, use the same value as the raw audio provided.
		// For WAV-specific params, just apply the default if no value is given.
		
		Integer sampleRate = format.getSampleRate();
		if (sampleRate == null)
			sampleRate = encodedFormat.getSampleRate();
		
		Integer numChannels = format.getNumChannels();
		if (numChannels == null)
			numChannels = encodedFormat.getNumChannels();
		
		WavEncodingType encoding = format.getWavEncoding();
		if (encoding == null)
			encoding = WavEncoder.DEFAULT_ENCODING;
		
		Short bitsPerSample = format.getBitsPerSample();
		if (bitsPerSample == null)
			bitsPerSample = WavEncoder.DEFAULT_BIT_DEPTH;
		
		format = new WavFormat(sampleRate, numChannels, encoding, 
				                                                bitsPerSample);

        sLogger.info("Final audio format will be " + format);
		
		if (!format.isEntirelyDetermined())
		{
			throw new InvalidAudioFormatException("Format is underspecified, " +		                                          
		                                          "and encoded audio is " +
					                              "missing complete format " +
		                                          "descriptor.");			
		}
		
		WavEncoder encoder = new WavEncoder(rawAudioData, format);
		mData = encoder.encodeToSource();
	}
	
	@Override
	public void buildFromAudio(DecodedAudio rawAudioData,
			                   AudioFormat format)
	    throws InvalidAudioFormatException, UnsupportedFormatException
	{
		buildFromAudio(rawAudioData,
					new WavFormat(format.getSampleRate(),
			                    format.getNumChannels(),
			                    WavEncoder.DEFAULT_ENCODING,
			                    WavEncoder.DEFAULT_BIT_DEPTH));
	}
}
