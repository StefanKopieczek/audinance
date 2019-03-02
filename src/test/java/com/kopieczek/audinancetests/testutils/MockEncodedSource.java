package com.kopieczek.audinancetests.testutils;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;

public class MockEncodedSource extends EncodedSource
{
	private byte[] mData;
	
	public MockEncodedSource(int[] data)
	{
		this(intsToBytes(data));
	}
	
	public MockEncodedSource(byte[] data)
	{
		mData = data;
	}
	
	@Override
	public byte getByte(int index) throws NoMoreDataException 
	{
		if (index < mData.length)
		{
			return mData[index];
		}
		else
		{
			throw new NoMoreDataException();
		}
	}

	@Override
	public int getLength() 
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	private static byte[] intsToBytes(int[] data)
	{
		byte[] result = new byte[data.length];
		
		for (int ii = 0; ii < data.length; ii++)
		{
			result[ii] = (byte)data[ii];
		}
		
		return result;
	}

}
