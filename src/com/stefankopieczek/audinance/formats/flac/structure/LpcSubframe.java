package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.utils.BitUtils;

public class LpcSubframe extends PredictiveSubframe
{
	private EncodedSource mSrc;
	
	private Frame mParent;
	
	private int mOrder;		
	
	private int[] mWarmUpSamples;
	
	private Integer mPrecision;
	
	private Integer mShift;
	
	private int[] mCoefficients;
		
	public LpcSubframe(EncodedSource src, Frame parent, int order)
	{		
		mSrc = src;
		mParent = parent;
		mOrder = order;
	}

	@Override
	public int getBodySize() 
	{		
		return mOrder * (mParent.getBitsPerSample() + getPrecision()) + 9 +
				getResidual().getSize();
	}
	
	private int getPrecision()
	{
		if (mPrecision == null)
		{
			int precisionIndex = mParent.getBitsPerSample() * mOrder;
			mPrecision = mSrc.intFromBits(precisionIndex, 4, ByteOrder.BIG_ENDIAN);
		}
		
		return mPrecision;
	}
	
	private int getShift()
	{
		// TODO: Definitely test this.
		if (mShift == null)
		{
			int shiftIndex = mParent.getBitsPerSample() * mOrder + 4;
			mShift = mSrc.intFromBits(shiftIndex, 5, ByteOrder.BIG_ENDIAN);
			
			mShift = BitUtils.uintTo2sComplement(mShift, 4);			
		}
		
		return mShift;
	}
	
	protected int[] getWarmUpSamples()
	{
		if (mWarmUpSamples == null)
		{
			mWarmUpSamples = new int[mOrder];						
			
			for (int ii = 0; ii < mOrder; ii++)
			{								
				int bitsPerSample = mParent.getBitsPerSample();
				int startIdx = ii * bitsPerSample;			
				mWarmUpSamples[ii] = mSrc.intFromBits(startIdx, 
						                              bitsPerSample, 
						                              ByteOrder.BIG_ENDIAN);
			}
		}
		
		return mWarmUpSamples;
	}
	
	protected int[] getCoefficients()
	{ 
		if (mCoefficients == null)
		{
			int shift = getShift();
			int precision = getPrecision();
			
			mCoefficients = new int[mOrder];
			
			int bitOffset = mOrder * mParent.getBitsPerSample() + 9;
			for (int ii = 0; ii < mOrder; ii++)
			{
				// TODO: Check the order these come out and make sure it's consistent
				// with the rest of the data.
				int startIdx = bitOffset + precision * ii;
				mCoefficients[ii] = mSrc.intFromBits(startIdx,
						                             precision,
						                             ByteOrder.BIG_ENDIAN);
				mCoefficients[ii] = 
						BitUtils.uintTo2sComplement(mCoefficients[ii], 
								                    precision - 1);
			}
		}		
				
		return mCoefficients;
	}

	@Override
	public double getPredictedSample(int idx) 
	{
		mWarmUpSamples = getWarmUpSamples();
		mCoefficients = getCoefficients();
		return 0; //TODO
	}

	@Override
	protected Residual getResidual() 
	{
		int residualStartIdx = 
				mOrder * (mParent.getBitsPerSample() + getPrecision()) + 9;
		
		return Residual.buildFromSource(mSrc.bitSlice(residualStartIdx), mParent, mOrder);
	}

	@Override
	protected int getOrder() 
	{
		return mOrder;
	}
}