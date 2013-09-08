package com.stefankopieczek.audinance.audiosources;
import com.stefankopieczek.audinance.*;
import java.io.*;

/**
 * Naïve implementation of EncodedAudioSource which simply reads all
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
	
	public byte getByte(int index)
	{
		return mData[index];
	}
	
}
