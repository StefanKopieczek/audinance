package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class RiceResidualPartition extends Residual
{
	private EncodedSource mSrc;
	
	private Frame mParentFrame;
	
	private int mSize;
	
	private int mParamBits;
	
	private int mPartitionOrder;
	
	private int mPredictorOrder;
	
	private boolean mIsFirstPartition;
	
	private Integer mRiceParameter;
	
	private int mEscapeCode;
	
	private Boolean mIsEscaped;
	
	private Integer mEscapedBitDepth;
	
	private Integer mNumSamples;
	
	private int[] mCorrections;
	
	public RiceResidualPartition(EncodedSource src,
								 Frame parentFrame,
			                     int predictorOrder,
			                     int partitionOrder,
			                     int paramBits,
			                     boolean isFirstPartition)
	{
		mSrc = src;
		mParentFrame = parentFrame;
		mParamBits = paramBits;		
		mEscapeCode = 2 ^ (mParamBits + 1) - 1;
		mPartitionOrder = partitionOrder;
		mPredictorOrder = predictorOrder;
		mIsFirstPartition = isFirstPartition;
	}
	
	private int getRiceParameter()
	{
		if (mRiceParameter == null && mIsEscaped != true)
		{
			int riceCode = mSrc.intFromBits(0, mParamBits, ByteOrder.BIG_ENDIAN);
			if (riceCode == mEscapeCode)
			{
				mIsEscaped = true;
			}
			else
			{
				mIsEscaped = false;
				mRiceParameter = riceCode;
			}
		}
		
		return mRiceParameter;
	}
	
	private boolean isEscaped()
	{
		if (mIsEscaped == null)
		{
			getRiceParameter();
		}
		
		return mIsEscaped;
	}
	
	private Integer getEscapedBitDepth()
	{
		Integer result = null;
		if (isEscaped())
		{
			if (mEscapedBitDepth == null)
			{
				mEscapedBitDepth = 
						mSrc.intFromBits(mParamBits, 5, ByteOrder.BIG_ENDIAN);
			}				
			
			result = mEscapedBitDepth;
		}
		
		return mEscapedBitDepth;
	}
	
	@Override
	public int getSize() 
	{
		if (mSize == 0)
		{
			// Since the corrections have variable encoding size, we have to
			// work them all out in order to determine the size of the block.
			// (This isn't true for escaped partitions, but that's a small edge
			// case and we don't lose much in ignoring it.)
			calculateCorrections();
		}
		
		return mSize;
	}
	
	public int getNumSamples()
	{
		if (mNumSamples == null)
		{
			if (mPartitionOrder == 0)
			{
				mNumSamples = mParentFrame.getBlockSize() - mPredictorOrder;
			}
			else if (!mIsFirstPartition)
			{
				mNumSamples = mParentFrame.getBlockSize() - (2 ^ mPartitionOrder);
			}
			else
			{
				// blocksize / (2^partitionOrder) - predictorOrder
				mNumSamples = (int)(((float)(mParentFrame.getBlockSize()) / (2 ^ mPartitionOrder)) - mPredictorOrder);
			}
		}
		
		return mNumSamples;		
	}

	@Override
	public int getCorrection(int idx) 
	{
		if (mCorrections == null)
		{
			calculateCorrections();
		}
		
		return mCorrections[idx];
	}
	
	private void calculateCorrections()
	{
		mCorrections = new int[getNumSamples()];		
				
		if (isEscaped())
		{
			// If the block is escaped (i.e. the residual is fixed-width 
			// encoded), then the samples are preceded by a 5-bit escape code
			// and a 5-bit precision indicator.
			int ptr = 10;
			int precision = getEscapedBitDepth();
			
			for (int sampleNum = 0; sampleNum < getNumSamples(); sampleNum++)
			{			
				mCorrections[sampleNum] = 
						mSrc.intFromBits(ptr, precision, ByteOrder.BIG_ENDIAN);
				ptr += precision;
			}
			
			mSize = 10 + precision * getNumSamples();
		}
		else
		{
			// The corrections are Rice encoded, so let's work them out.
			int ptr = 5;
			int sampleNum = 0;
			
			while (sampleNum < getNumSamples())
			{
				// The most-significant bits of the residual are unary-encoded.
				int residual = -1;
				int currentVal = 0;
				
				while (currentVal != 1)
				{
					residual += 1;
					currentVal = mSrc.getBit(ptr);
					ptr += 1;
				}							
				
				residual <<= getRiceParameter();
				
				// The least-significant bits follow, taking up exactly
				// rice-parameter-minus-one-many bits.
				residual += mSrc.intFromBits(ptr, 
						                     getRiceParameter() - 1, 
						                     ByteOrder.BIG_ENDIAN);				
				
				// Finally, there is a sign-bit.
				if (mSrc.getBit(ptr + getRiceParameter() - 1) == 1)
				{
					residual = residual * -1 - 1;
				}
				
				ptr += getRiceParameter();				
						
				mCorrections[sampleNum] = residual;
				sampleNum ++;
			}
			
			mSize = 5 + ptr;
		}
	}
}
