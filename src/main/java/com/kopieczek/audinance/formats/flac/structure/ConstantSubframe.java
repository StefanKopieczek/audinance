package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public class ConstantSubframe extends Subframe
{
	private Integer mValue;
	
	public ConstantSubframe(EncodedSource src, Frame parent)
	{
		super(src, parent);
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
			mValue = mSrc.intFromBits(getHeaderSize(), getSize(), ByteOrder.BIG_ENDIAN);
		}
		
		return mValue;
	}
}