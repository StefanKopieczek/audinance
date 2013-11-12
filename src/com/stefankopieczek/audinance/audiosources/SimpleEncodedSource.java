package com.stefankopieczek.audinance.audiosources;
import com.stefankopieczek.audinance.*;
import com.stefankopieczek.audinance.utils.AudinanceUtils;

import java.io.*;

/**
 * Naive implementation of EncodedAudioSource which simply reads all
 * audio data into memory.
 */
public class SimpleEncodedSource extends EncodedSource
{
	private byte[] mData;
	
	public SimpleEncodedSource(InputStream is)
		throws IOException
	{
		mData = AudinanceUtils.getByteArrayFromStream(is);
	}
	
	public byte getByte(int index) throws NoMoreDataException
	{		
		if (index < mData.length)
		{	
			return mData[index];
		}
		else
		{
			System.out.println("Done!!!");
			throw new NoMoreDataException();
		}
	}	
}
