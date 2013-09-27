package com.stefankopieczek.audinance.formats.wav;
import com.stefankopieczek.audinance.formats.*;
import java.io.*;

public class WavData extends EncodedAudio
{	
	private static final WavEncodingType DEFAULT_ENCODING = 
			                                               WavEncodingType.PCM;
	private static final short DEFAULT_BIT_DEPTH = 16;
	
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
	
	public WavData(EncodedAudio encodedAudio, WavFormat format) 
		throws IOException, 
		       InvalidAudioFormatException, 
		       UnsupportedFormatException
	{
		super(encodedAudio, format);		
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
	                           WavFormat format) 
	    throws InvalidAudioFormatException, UnsupportedFormatException
	{		
		AudioFormat encodedFormat = encodedAudio.getFormat();
		
		Integer sampleRate = format.getSampleRate();
		if (sampleRate == null)
			sampleRate = encodedFormat.getSampleRate();
		
		Integer numChannels = format.getNumChannels();
		if (numChannels == null)
			numChannels = encodedFormat.getNumChannels();
		
		WavEncodingType encoding = format.getWavEncoding();
		if (encoding == null)
			encoding = DEFAULT_ENCODING;
		
		Short bitsPerSample = format.getBitsPerSample();
		if (bitsPerSample == null)
			bitsPerSample = DEFAULT_BIT_DEPTH;
		
		format = new WavFormat(sampleRate, numChannels, encoding, 
				                                                bitsPerSample);
		
		if (!format.isEntirelyDetermined())
		{
			throw new InvalidAudioFormatException("Format is underspecified, " +		                                          
		                                          "and encoded audio is " +
					                              "missing complete format " +
		                                          "descriptor.");			
		}
	}
	
	public void buildFromAudio(EncodedAudio encodedAudio,
			                   AudioFormat format) 
        throws InvalidAudioFormatException, UnsupportedFormatException
	{
		buildFromAudio(encodedAudio,
				       new WavFormat(format.getSampleRate(),
				                     format.getNumChannels(),
				                     DEFAULT_ENCODING,
				                     DEFAULT_BIT_DEPTH));
	}
	
	@Override
	public void buildFromAudio(DecodedAudio rawAudioData,
	                           AudioFormat audioFormat)
	{
		// todo
	}
}
