package com.kopieczek.audinance.formats.flac;

import com.kopieczek.audinance.formats.*;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Represents a FLAC audio clip, undecoded, backed by data drawn from some
 * abstract source.
 */
public class FlacData extends EncodedAudio
{
	private static final Logger sLogger = Logger.getLogger(FlacData.class.getName());

 	/**
	 * The format of the FLAC data. This includes recording information,
	 * as the sample rate, but also datatype-specific parameters such as
	 * the FLAC blocksize.
	 */
	private FlacFormat mFormat;

	/**
	 * Constructs a FLAC clip backed by the given .flac file.
	 * 
	 * @param file The flac file to build this clip from.
	 * @throws FileNotFoundException if the specified wav file does not exist.
	 * @throws IOException if we fail to read from the file.
	 * @throws InvalidAudioFormatException TODO SMK
	 */
	public FlacData(File file)
	throws FileNotFoundException, 
	IOException,
	InvalidAudioFormatException
	{
		super(file);
        sLogger.info("Constructed FlacData object for file " + file);
	}

	/**
	 * Constructs a FLAC clip backed by the given input stream.
	 *
	 * @param is The flac stream to build this clip from.
	 * @throws IOException if we fail to read from the stream.
	 * @throws InvalidAudioFormatException TODO SMK
	 */
	public FlacData(InputStream is) 
	throws IOException, InvalidAudioFormatException
	{
		super(is);
        sLogger.info("Constructed FlacData object for input stream " + is);
	}

	/**
	 * Constructs a FLAC clip from a clip of encoded audio.
	 * This will usually involve first decoding the clip, and then
	 * re-encoding it in FLAC format.
	 * Recoding may happen up-front or 'just in time'.
	 *
	 * @param encodedAudio The audio on which to base this FLAC clip.
	 * @param format The recording format in which to store the resultant
	 *               flac data.
	 * @throws InvalidAudioFormatException If the specified audio format is underdetermined.
	 * @throws UnsupportedFormatException SMK todo
	 */
	public FlacData(EncodedAudio encodedAudio, AudioFormat format)
	throws InvalidAudioFormatException, 
	UnsupportedFormatException
	{
		super(encodedAudio, format);
        sLogger.info("Constructed FlacData object for encoded audio " + encodedAudio + " with target format " + format);
	}

	/**
	 * Takes the given <tt>EncodedSource</tt> containing valid FLAC data
	 * and a format object describing how the data is to be interpreted,
	 * and builds a new <tt>FlacData</tt> object.
	 *
	 * @param flacSource A source of valid FLAC data.
	 * @param format The recording format of the data provided.
	 */
	public FlacData(EncodedSource flacSource, FlacFormat format)
	{		
		mData = flacSource;
		mFormat = format;
        sLogger.info("Constructed FlacData object for flac source " + flacSource + " with target format " + format);
	}

	@Override
	public DecodedAudio getDecodedAudio()
	{
	    sLogger.info("Decoding " + this);
		FlacDecoder flacDecoder = new FlacDecoder(getSource());
		return flacDecoder.getDecodedAudio();
	}

	@Override
	public DataType getDataType()
	{
		return DataType.FLAC;
	}

	@Override
	public FlacFormat getFormat()
	{
		if (mFormat == null)
		{
			FlacDecoder flacDecoder = new FlacDecoder(getSource());
			mFormat = flacDecoder.getFormat();
		}

		return mFormat;
	}

	@Override
	public void buildFromAudio(EncodedAudio encodedAudio,
			                   AudioFormat format) 
	throws InvalidAudioFormatException, UnsupportedFormatException
	{
		if (format instanceof FlacFormat)
		{
			// Use the WAV format instructions if provided.
			// We can't do this with inheritance as I'm rubbish. TODO.
			buildFromAudio(encodedAudio.getDecodedAudio(), (FlacFormat)format);
		}
		else
		{
			// Allow the encoder to choose the blocksize parameters.
			buildFromAudio(encodedAudio,
					       new FlacFormat(format.getSampleRate(),
					                     format.getNumChannels(),
										 null,
										 null));
		}
	}

	private static FlacData buildFromAudio(DecodedAudio rawAudioData,
	                                       FlacFormat format)
	throws InvalidAudioFormatException
	{
		AudioFormat encodedFormat = rawAudioData.getFormat();

		// For each format parameter, if the given format does not explicitly
		// state the desired value, use the same value as the raw audio provided.
		// For FLAC-specific params, just apply the default if no value is given.

		Integer sampleRate = format.getSampleRate();
		if (sampleRate == null)
			sampleRate = encodedFormat.getSampleRate();

		Integer numChannels = format.getNumChannels();
		if (numChannels == null)
			numChannels = encodedFormat.getNumChannels();

		// Allow the block size specifications to be null.
		// The encoder will choose the best values itself if they are
		// not provided.
		format = new FlacFormat(sampleRate, 
		                        numChannels,
								format.getMinBlockSize(),
								format.getMaxBlockSize());

		if (!format.isEntirelyDetermined())
		{
			throw new InvalidAudioFormatException("Format is underspecified, " +		                                          
		                                          "and encoded audio is " +
					                              "missing complete format " +
		                                          "descriptor.");			
		}

		FlacEncoder encoder = new FlacEncoder(rawAudioData, format);
		return encoder.encodeToSource();
	}

	@Override
	public void buildFromAudio(DecodedAudio rawAudioData,
			                   AudioFormat format)
	throws InvalidAudioFormatException, UnsupportedFormatException
	{
		// Allow the encoder to choose the best values for the block size range.
		buildFromAudio(rawAudioData,
					   new FlacFormat(format.getSampleRate(),
									  format.getNumChannels(),
									  null,
									  null));
	}
}
