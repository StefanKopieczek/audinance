package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

public class ApplicationBlock extends MetadataBlock
{
	private int mApplicationId;
	
	private EncodedSource mData;

	public ApplicationBlock(int length, EncodedSource src)
	{
		super(length);
		mApplicationId = src.intFromBits(0, 32, ByteOrder.BIG_ENDIAN);
		mData = src.bitSlice(32);
	}
	
	public ApplicationBlock(EncodedSource data,
							int applicationId)
	{
		super(data.getLength() + 7);
		mApplicationId = applicationId;
		mData = data;
	}
	
	public int getApplicationId()
	{
		return mApplicationId;
	}
	
	public EncodedSource getData()
	{
		return mData;
	}
}
