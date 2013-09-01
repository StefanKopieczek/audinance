package com.stefankopieczek.audinance;

public class AudioFormat
{
	private final Integer mSampleRate;
	private final Integer mBitDepth;
	private final Integer mChannels;
	
	public AudioFormat(Integer sampleRate,
	 				   Integer bitDepth,
					   Integer channels)
	{
		mSampleRate = sampleRate;
		mBitDepth = bitDepth;
		mChannels = channels;
	}
	
	@Override
	public String toString()
	{
		String sampleString = "sample rate: " + mSampleRate + "Hz";
		String depthString = "bit depth: " + mBitDepth+ " bits";
		String channelString = mChannels + " channels";
		
		return "(AudioFormat - " + sampleString + ", " +
		       depthString + ", " +
               channelString + ")";
	}
	
	public boolean isEntirelyDefined()
	{
		boolean isDefined = (mSampleRate != null);
		isDefined &= (mBitDepth != null);
		isDefined &= (mChannels != null);
		
		return isDefined;
	}
}
