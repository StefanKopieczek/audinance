package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class StreamInfoBlock extends MetadataBlock
{
	public static final int STREAM_INFO_LENGTH_BITS = 272;
	
	private EncodedSource mDataSource;
	
	private Integer mMinimumBlockSize = null;
	
	private Integer mMaximumBlockSize = null;
	
	private Integer mMinimumFrameSize = null;
	
	private Integer mMaximumFrameSize = null;
	
	private Integer mSampleRate = null;
	
	private Integer mNumChannels = null;
	
	private Integer mBitsPerSample = null;
	
	private Integer mNumTotalSamples = null;
	
	private byte[] mChecksum = null;
	
	public StreamInfoBlock(EncodedSource src)
	{
		super(STREAM_INFO_LENGTH_BITS);
		mDataSource = src;
	}
	
	public StreamInfoBlock(int minimumBlockSize, int maximumBlockSize,
						   int minimumFrameSize, int maximumFrameSize,
						   int sampleRate,
						   int numChannels,
						   int bitsPerSample)
	{
		super(STREAM_INFO_LENGTH_BITS);
		mMinimumBlockSize = minimumBlockSize;
		mMaximumBlockSize = maximumBlockSize;
		mMinimumFrameSize = minimumFrameSize;
		mMaximumFrameSize = maximumFrameSize;
		mSampleRate = sampleRate;
		mNumChannels = numChannels;
		mBitsPerSample = bitsPerSample;
	}
	
	public int getMinimumBlockSize()
	{
		if (mMinimumBlockSize == null)
		{
			mMinimumBlockSize = intFromBits(0, 16);
		}
		
		return mMinimumBlockSize.intValue();
	}
	
	public int getMaximumBlockSize()
	{
		if (mMaximumBlockSize == null)
		{
			mMaximumBlockSize = intFromBits(16, 16);
		}
		
		return mMaximumBlockSize.intValue();
	}
	
	public int getMinimumFrameSize()
	{
		if (mMinimumFrameSize == null)
		{
			mMinimumFrameSize = intFromBits(32, 24);
		}
		
		return mMinimumFrameSize.intValue();
	}
	
	public int getMaximumFrameSize()
	{
		if (mMaximumFrameSize == null)
		{
			mMaximumFrameSize = intFromBits(56, 24);
		}
		
		return mMaximumFrameSize.intValue();
	}
	
	public int getSampleRate()
	{
		if (mSampleRate == null)
		{
			mSampleRate = intFromBits(80, 20);
		}
		
		return mSampleRate.intValue();
	}
	
	public int getNumChannels()
	{
		if (mNumChannels == null)
		{
			// Flac files store (num channels)-1
			mNumChannels = intFromBits(100, 3) + 1;
		}
		
		return mNumChannels.intValue();
	}
	
	public int getBitsPerSample()
	{
		if (mBitsPerSample == null)
		{
			// Flac files store (bits per sample)-1
			mBitsPerSample = intFromBits(103, 5) + 1;
		}
		
		return mBitsPerSample.intValue();
	}
	
	public int getNumTotalSamples()
	{
		if (mNumTotalSamples == null)
		{
			mNumTotalSamples = intFromBits(108, 36);
		}
		
		return mNumTotalSamples.intValue();
	}
	
	public byte[] getChecksum()
	{
		if (mChecksum == null)
		{
			mChecksum = mDataSource.bitSlice(144, 128).toArray();
		}
		
		return mChecksum;
	}
	
	private int intFromBits(int idx, int length)
	{
		return mDataSource.intFromBits(idx, length, ByteOrder.LITTLE_ENDIAN);
	}
}
