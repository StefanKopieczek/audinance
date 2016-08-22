package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class ConstantSubframe extends Subframe
{
	private EncodedSource mSrc;
	
	private Frame mParent;
	
	private Integer mValue;
	
	public ConstantSubframe(EncodedSource src, Frame parent)
	{		
		mSrc = src;
		mParent = parent;
	}
	
	@Override
	public int getBodySize() 
	{
		return mParent.getBitsPerSample();
	}	
	
	@Override
	public double getSample(int idx)
	{
		if (mValue == null)
		{
			mValue = mSrc.intFromBits(0, getSize(), ByteOrder.BIG_ENDIAN);
		}
		
		return mValue;
	}
}