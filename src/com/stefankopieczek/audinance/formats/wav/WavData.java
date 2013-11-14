package com.stefankopieczek.audinance.formats.wav;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.*;
import java.io.*;

public class WavData extends EncodedAudio
{			
	private WavFormat mFormat;
	
	public WavData(File file)
		throws FileNotFoundException, 
		       IOException,
			   InvalidAudioFormatException
	{
		super(file);		
	}
	
	public WavData(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		super(is);
	}
	
	public WavData(EncodedAudio encodedAudio, AudioFormat format) 
		throws InvalidAudioFormatException, 
		       UnsupportedFormatException
	{
		super(encodedAudio, format);		
	}
	
	public WavData(EncodedSource wavSource, WavFormat format)
	{		
		mData = wavSource;
		mFormat = format;
	}

	public DecodedAudio getDecodedAudio() 
        throws InvalidWavDataException, UnsupportedWavEncodingException 
	{
		WavDecoder wavDecoder = new WavDecoder(getSource());
		return wavDecoder.getDecodedAudio();
		
	}

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
	
	public void buildFromAudio(DecodedAudio rawAudioData,
	                           WavFormat format)
		throws InvalidAudioFormatException
	{
		AudioFormat encodedFormat = rawAudioData.getFormat();
		
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
