package com.kopieczek.audinance.formats.wav;

import com.kopieczek.audinance.formats.AudioFormat;

public class WavFormat extends AudioFormat
{
	private WavEncodingType mEncoding;
	private Short mBitsPerSample;
	
	public WavFormat(Integer sampleRate,
			         Integer channels,
			         WavEncodingType encoding,
			         Short bitsPerSample)
	{
		super(sampleRate, channels);
		mEncoding = encoding;
		mBitsPerSample = bitsPerSample;
	}
	
	public WavEncodingType getWavEncoding()
	{
		return mEncoding;			
	}
	
	public Short getBitsPerSample()
	{
		return mBitsPerSample;
	}
	
	public boolean isEntirelyDetermined()
	{
		return (super.isEntirelyDetermined() &&
				mEncoding != null &&
				mBitsPerSample != null);
	}
	
	@Override
	public String toString()
	{
		String sampleString = "sample rate: " + getSampleRate() + "Hz";
		String channelString = getNumChannels() + " channels";
		String encoding = getWavEncoding().toString();
		String bitDepth = "bit depth: " + getBitsPerSample();
		
		return "(WavFormat - " + 
		       sampleString + ", " +
               channelString + ", " +
		       encoding + ", " +
               bitDepth + ")";
	}
}
