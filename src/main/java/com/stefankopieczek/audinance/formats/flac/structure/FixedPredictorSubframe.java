package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.flac.InvalidFlacDataException;

import java.nio.ByteOrder;

public class FixedPredictorSubframe extends PredictiveSubframe
{
	private final int mOrder;
	
	private int[] mWarmUpSamples;
	
	private int[] mCoefficients;	
	
	public FixedPredictorSubframe(EncodedSource src, Frame parent, int order)
	{		
		super(src, parent);
		mOrder = order;
	}
	
	protected int getOrder()
	{
		return mOrder;
	}
	
	@Override
	public int getBodySize() 
	{
		return mParent.getBitsPerSample() * mOrder + getResidual().getSize();
	}	
	
	protected int[] getWarmUpSamples()
	{
		if (mWarmUpSamples == null)
		{
			mWarmUpSamples = new int[mOrder];						
			
			for (int ii = 0; ii < mOrder; ii++)
			{								
				int bitsPerSample = mParent.getBitsPerSample();
				int startIdx = getHeaderSize() + ii * bitsPerSample;
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
			mCoefficients = new int[mOrder];
			
			switch (mOrder)
			{
				case 0:
					// Zeroth-order predictor has no coefficients.
					break;
				case 1:
					// First-order predictor just echoes previous sample.
					mCoefficients[0] = 1;
					break;
				case 2:
					// Second-order predictor fits a line to the previous two
					// samples: 2x_{n-1} - x_{n-2}.
					mCoefficients[0] = -1;
					mCoefficients[1] = 2;
					break;
				case 3:
					// Third-order predictor fits a parabola.
					// 3x_{n-1} - 3x_{n-2} + x_{n-3}.
					mCoefficients[0] = 1;
					mCoefficients[1] = -3;
					mCoefficients[2] = 3;
					break;
				case 4:
					// Fourth-order predictor fits a cubic.
					// TODO: Derive 4th-order predictor.
					throw new RuntimeException("Not yet implemented");
					// break;
				default:
                    throw new InvalidFlacDataException("Unknown fixed predictor order " + mOrder);
			}						
		}
		
		return mCoefficients;
	}	

	@Override
	protected Residual getResidual() 
	{
		int residualStartIdx = getHeaderSize() + mOrder * mParent.getBitsPerSample();
		
		return Residual.buildFromSource(mSrc.bitSlice(residualStartIdx), mParent, mOrder);
	}		
}