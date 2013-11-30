package com.stefankopieczek.audinance.audiosources;
import com.stefankopieczek.audinance.utils.AudinanceUtils;

import java.io.*;

/**
 * Naive implementation of EncodedAudioSource which simply reads all
 * audio data into memory.
 */
public class SimpleEncodedSource extends EncodedSource
{
	/**
	 * The array containing the data read from the underlying source.
	 */
	private byte[] mData;
	
	public SimpleEncodedSource(InputStream is)
		throws IOException
	{
		// Immediately pull all data from the stream into memory.
		mData = AudinanceUtils.getByteArrayFromStream(is);
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
		return mData.length;
	}

	@Override
	public String toString()
    {
        return "<SimpleEncodedSource - " + mData.length + " bytes>";
    }
}
