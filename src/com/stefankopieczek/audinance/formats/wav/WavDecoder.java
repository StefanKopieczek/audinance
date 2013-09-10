package com.stefankopieczek.audinance.formats.wav;

import java.io.InputStream;

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
		
		protected int getStartIndex()
		{
			return mStartIdx.intValue();
		}
		
		protected int getEndIndex()
		{
			if (mEndIdx == 0)
				mEndIdx = mStartIdx + getLength();
				
			return mEndIdx.intValue();
		}
		
		protected int getLength()
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
		private static final int FORMAT_CODE_IDX_OFFSET = 8;
		private static final int NUM_CHANNELS_IDX_OFFSET = 10;
		private static final int SAMPLE_RATE_IDX_OFFSET = 12;
		private static final int BYTE_RATE_IDX_OFFSET = 16;
		private static final int BLOCK_ALIGN_IDX_OFFSET = 20;
		private static final int BITS_PER_SAMPLE_IDX_OFFSET = 22;
		private static final int EXTRA_PARAMS_SIZE_IDX_OFFSET = 24;
		private static final int EXTRA_PARAMS_IDX_OFFSET = 26;
		
		private Integer mFormatCode;
		private Integer mNumChannels;
		private Integer mSampleRate;
		private Integer mByteRate;
		private Integer mBlockAlign;
		private Integer mBitsPerSample;
		private Integer mExtraParamsSize;

		public FmtSubchunk(int startIdx)
		{
			super(startIdx);
		}

		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}				
		
		public int getFormatCode()
		{
			if (mFormatCode == null)
			{
				mFormatCode = 
					getIntFromBytes(getStartIndex() + FORMAT_CODE_IDX_OFFSET, 4);
			}
			
			return mFormatCode.intValue();
		}
		
		public WavEncodingType getFormat()
			throws UnsupportedWavEncodingException
		{
			return WavEncodingType.getEncodingTypeFromCode(getFormatCode());
		}
		
		public int getNumChannels()
		{
			if (mNumChannels == null)
			{
				mNumChannels = 
			     getIntFromBytes(getStartIndex() + NUM_CHANNELS_IDX_OFFSET, 4);
			}
			
			return mNumChannels.intValue();
		}
				
		public int getSampleRate()
		{
			if (mSampleRate == null)
			{
				mSampleRate = 
			      getIntFromBytes(getStartIndex() + SAMPLE_RATE_IDX_OFFSET, 4);
			}
			
			return mSampleRate.intValue();
		}
		
		public int getByteRate()
		{
			if (mByteRate == null)
			{
				mByteRate = 
			        getIntFromBytes(getStartIndex() + BYTE_RATE_IDX_OFFSET, 4);
			}
			
			return mByteRate.intValue();
		}
		
		public int getBlockAlign()
		{
			if (mBlockAlign == null)
			{
				mBlockAlign = 
			      getIntFromBytes(getStartIndex() + BLOCK_ALIGN_IDX_OFFSET, 4);
			}
			
			return mBlockAlign.intValue();
		}
		
		public int getBitsPerSample()
		{
			if (mBitsPerSample == null)
			{
				mBitsPerSample = 
			      getIntFromBytes(getStartIndex() + BLOCK_ALIGN_IDX_OFFSET, 4);
			}
			
			return mBitsPerSample.intValue();
		}
	}
	
	private class DataSubchunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;
		private static final int DATA_IDX_OFFSET = 8;			

		public DataSubchunk(int startIdx)
		{
			super(startIdx);
		}

		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}
		
		public InputStream getDataStream()
		{			
			return new InputStream()
			{
				private final int mStartIdx= getStartIndex() + DATA_IDX_OFFSET;
				private final int mEndIdx = getStartIndex() + getLength();
				private int mCurrentIdx = mStartIdx; 
				
				public int read()
				{
					int result = -1;
					
					if (mCurrentIdx < mEndIdx)
					{
						mCurrentIdx += 1;					
						result = mWavSource.getByte(mCurrentIdx);
					}
					
					return result;
				}
			};
		}
	}
}
