package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

public class SeektableBlock extends MetadataBlock
{
	private EncodedSource mDataSource;
	
	private int mNumRows;
	
	public SeektableBlock(int length, EncodedSource src)
	{
		super(length);
		mNumRows = length / SeekPoint.SIZE;
		mDataSource = src;
	}
	
	public SeekPoint getPoint(int row)
	{
		return new SeekPoint(
			src.bitSlice(row * SeekPoint.SIZE, SeekPoint.SIZE));
	}
	
	public SeekPoint findPoint(long sampleIdx)
	{
		int bestRowPtr = 0;
		long bestIndex = mDataSource.longFromBits(0, 64, ByteOrder.BIG_ENDIAN);
		
		if (bestIndex > sampleIndex)
			return null;
		
		for (int rowPtr = SeekPoint.SIZE; 
		     rowPtr < mLength; 
			 rowPtr += SeekPoint.SIZE)
		{
			long newIndex = mDataSource.longFromBits(rowPtr, 64, ByteOrder.BIG_ENDIAN);
			
			if (newIndex > sampleIdx)
				break;
				
			bestIndex = newIndex;
			bestRowPtr = rowPtr;
		}
		
		return new SeekPoint(src.bitSlice(bestRowPtr, SeekPoint.SIZE));
	}
	
	public static class SeekPoint
	{
		public static final int SIZE = 144;
		public static final long PLACEHOLDER = 0xffffffffffffffff;
		public final long mSampleIdx;
		public final long mOffset;
		public final int mNumSamples;
	
		public SeekPoint(EncodedSource src)
		{
			mSampleIdx = src.longFromBits(0, 64);
			mOffset = src.longFromBits(64, 64);
			mNumSamples = src.intFromBits(128, 16);
		}
	
		public SeekPoint(sampleIdx, offset, numSamples)
		{
			mSampleIdx = sampleIdx;
			mOffset = offset;
			mNumSamples = numSamples;
		}
	}
}
