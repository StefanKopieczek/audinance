package com.kopieczek.audinance.formats.flac;

import com.kopieczek.audinance.formats.AudioFormat;

public class FlacFormat extends AudioFormat
{
	private Integer mMinBlockSize;
	
	private Integer mMaxBlockSize;
	
	public FlacFormat(Integer sampleRate, 
	                  Integer numChannels,
					  Integer minBlockSize,
					  Integer maxBlockSize)
	{
		super(sampleRate, numChannels);
		mMinBlockSize = minBlockSize;
		mMaxBlockSize = maxBlockSize;
	}
	
	public Integer getMinBlockSize()
	{
		return mMinBlockSize;
	}
	
	public Integer getMaxBlockSize()
	{
		return mMaxBlockSize;
	}
	
	public Boolean isFixedBlockSize()
	{
		if (mMinBlockSize == null || mMaxBlockSize == null)
		{
			return null;
		}
		else
		{
			return mMinBlockSize.equals(mMaxBlockSize);
		}
	}
	
	@Override
	public String toString()
	{
		String sampleString = "sample rate: " + getSampleRate() + "Hz";
		String channelString = getNumChannels() + " channels";
		
		String blockSizeString = isFixedBlockSize() ?
			("fixed blocksize: " + getMinBlockSize()) :
			("variable blocksize: " + getMinBlockSize() + "-" + getMaxBlockSize());

		return "(FlacFormat - " + 
			sampleString + ", " +
			channelString + ", " +
			blockSizeString + ")";
	}
}
