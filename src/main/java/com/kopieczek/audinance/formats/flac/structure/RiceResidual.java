package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class RiceResidual extends Residual 
{
	private final EncodedSource mSrc;
	
	private final int mParamBits;
	
	private final int mPartitionOrder;
	
	private final RiceResidualPartition[] mPartitions;
	
	private int mSize;
	
	public RiceResidual(EncodedSource src,
			            Frame parentFrame,
			            int predictorOrder,
			            int paramBits) 
	{
		mSrc = src;		
		mParamBits = paramBits;
		
		mPartitionOrder = mSrc.intFromBits(0, 4, ByteOrder.BIG_ENDIAN);
		mPartitions = new RiceResidualPartition[2 ^ mPartitionOrder];
		
		int ptr = 4;
		for (int ii = 0; ii < mPartitions.length; ii++)
		{
			mPartitions[ii] = new RiceResidualPartition(mSrc.bitSlice(ptr),
					                                    parentFrame,
					                                    predictorOrder,
					                                    mPartitionOrder,
					                                    mParamBits,
					                                    ii == 0);
			ptr += mPartitions[ii].getSize();
			mSize += mPartitions[ii].getSize();
		}
	}

	@Override
	public int getSize() 
	{
		return mSize;
	}

	@Override
	public int getCorrection(int idx) 
	{
		// Somewhat inefficient. Possibly something to come back to if 
		// efficiency becomes a problem.
		int samplesPassed = 0;
		for (RiceResidualPartition partition : mPartitions)
		{						
			if (samplesPassed + partition.getNumSamples() > idx)
			{
				return partition.getCorrection(idx - samplesPassed);
			}
			
			samplesPassed += partition.getNumSamples();
		}
		
		throw new IndexOutOfBoundsException("Requested correction number " + idx +
				                            "was out of range for " + this);
	}

}
