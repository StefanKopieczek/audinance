package com.stefankopieczek.audinance;

/**
 * Stores datatype-independent audio format information.
 * Values can be either specified or null; the latter is used when
 * specifying the desired format for a conversion, and indicates that
 * the converter is free to choose any value it wants.
 *
 * @author Stefan Kopieczek
 */
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
	
	/**
	 * Determines if this <tt>AudioFormat</tt> is completely 
	 * determined; i.e. no members are <tt>null</tt>.
	 * (Null values indicate we don't insist on a particular
	 *  format when deconverting, but this is not always appropriate.)
	 * 
	 * @return True if no members of this class are <tt>null</tt>.
	 */
	public boolean isEntirelyDetermined()
	{
		boolean isDefined = (mSampleRate != null);
		isDefined &= (mBitDepth != null);
		isDefined &= (mChannels != null);
		
		return isDefined;
	}
	
	public Integer getSampleRate()
	{
		return mSampleRate;
	}
	
	public Integer getBitDepth()
	{
		return mBitDepth;
	}
	
	public Integer getNumChannels()
	{
		return mChannels;
	}
	
	public Integer getFrameRate()
	{
		if (mSampleRate == null || mChannels == null)
			return null;
			
		return mSampleRate * mChannels;
	}
	
	public Integer getBitRate()
	{
		Integer frameRate = getFrameRate();
		
		if (frameRate == null || mBitDepth == null)
			return null;
			
		return frameRate * mBitDepth;
	}
}
