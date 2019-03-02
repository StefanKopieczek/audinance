package com.kopieczek.audinance.formats;

import com.kopieczek.audinance.utils.BitUtils;

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
	private final Integer mChannels;
	
	public AudioFormat(Integer sampleRate,
					   Integer channels)
	{
		mSampleRate = sampleRate;
		mChannels = channels;
	}
	
	@Override
	public String toString()
	{
		String sampleString = "sample rate: " + mSampleRate + "Hz";
		String channelString = mChannels + " channels";
		
		return "<AudioFormat - " + sampleString + ", " +
               channelString + ">";
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof AudioFormat))
			return false;
		
		AudioFormat otherFormat = (AudioFormat)other;
		
		boolean sampleRatesEqual = (BitUtils.nullsafeEquals(
				                    otherFormat.getSampleRate(), mSampleRate));
		boolean numChannelsEqual = (BitUtils.nullsafeEquals(
                                    otherFormat.getNumChannels(), mChannels));
		
		return sampleRatesEqual && numChannelsEqual;
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
		isDefined &= (mChannels != null);
		
		return isDefined;
	}
	
	/**
	 * Gets the sample rate of the audio data; that is, the number
	 * of audio samples per channel per second.
	 *
	 * @return The sample rate in Hz, or <tt>null</tt> if no
	 * sample rate is specified (this indicates that we do not
	 * mandate a particular sample rate when this format is used to
	 * specify the result of an audio conversion).
	 */
	public Integer getSampleRate()
	{
		return mSampleRate;
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
	
	public javax.sound.sampled.AudioFormat getJmfAudioFormat()
	{
		// Need to make this a bit more general. TODO.
		return new javax.sound.sampled.AudioFormat((float)getSampleRate(),
		                                           16,
		                                           getNumChannels().intValue(),
		                                           true,
		                                           false);		
	}
}
