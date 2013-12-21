package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

public class RiceResidualPartition extends Residual
{
	private EncodedSource mSrc;
	
	private Frame mParentFrame;
	
	private int mParamBits;
	
	private int mPartitionOrder;
	
	private int mPredictorOrder;
	
	private boolean mIsFirstPartition;
	
	private Integer mRiceParameter;
	
	private int mEscapeCode;
	
	private Boolean mIsEscaped;
	
	private Integer mEscapedBitDepth;
	
	private Integer mNumSamples;
	
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
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}
	
}
