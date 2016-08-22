package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class VerbatimSubframe extends Subframe
{
	private EncodedSource mSrc;
	
	private Frame mParent;	
		
	public VerbatimSubframe(EncodedSource src, Frame parent)
	{
		mSrc = src;
		mParent = parent;
	}

	@Override
	public int getBodySize() 
	{
		return mParent.getBitsPerSample() * mParent.getBlockSize();
	}

	@Override
	public double getSample(int idx) 
	{
		int blockSize = mParent.getBlockSize();
		if (idx > blockSize)
		{
			throw new IndexOutOfBoundsException("Index " + idx + " requested " +
		                                        "from subframe " + this + 
		                                        "; max is " + (blockSize - 1));
		}
		
		return mSrc.intFromBits(idx * blockSize, blockSize, ByteOrder.BIG_ENDIAN);
	}
}