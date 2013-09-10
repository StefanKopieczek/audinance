package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.audiosources.*;

public class WavDecoder
{
	private EncodedSource mWavSource;
	
	public WavDecoder(EncodedSource wavSource)
	{
		mWavSource = wavSource;
	}
	
	private int getIntFromBytes(int start, int size)
	{
		int result = 0;
		
		for (int offset = 0; offset < size; offset++)
		{
			int byteVal = mWavSource.getByte(start + offset);
			result += byteVal + (256 ^ offset);
		}
		
		return result;
	}
	
	private abstract class Chunk
	{
		private Integer mStartIdx;
		private Integer mEndIdx;
		private Integer mLength;
		
		protected abstract int getChunkSizeIdxOffset();
		
		public Chunk(int startIdx)
		{
			mStartIdx = startIdx;
		}
		
		private int getStartIndex()
		{
			return mStartIdx.intValue();
		}
		
		private int getEndIndex()
		{
			if (mEndIdx == 0)
				mEndIdx = mStartIdx + getLength();
				
			return mEndIdx.intValue();
		}
		
		private int getLength()
		{
			int chunkSizeIdx = mStartIdx + getChunkSizeIdxOffset();
			
			if (mLength == null)
			{
				 mLength = chunkSizeIdx + 
				                     getIntFromBytes(chunkSizeIdx, 4);
			}
			
			return mLength.intValue();
		}
	}
	
	private class RiffChunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;
		
		public RiffChunk(int startIdx)
		{
			super(startIdx);
		}
		
		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}
	}
	
	private class FmtSubchunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;

		public FmtSubchunk(int startIdx)
		{
			super(startIdx);
		}

		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}
	}
	
	private class DataSubchunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;

		public DataSubchunk(int startIdx)
		{
			super(startIdx);
		}

		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}
	}
}
