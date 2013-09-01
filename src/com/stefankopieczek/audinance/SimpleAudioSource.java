package com.stefankopieczek.audinance;
import java.io.*;

public class SimpleAudioSource extends AudioSource
{
	private byte[] mData;
	
	public SimpleAudioSource(InputStream is)
		throws IOException
	{
		mData = AudinanceUtils.getByteArrayFromStream(is);
	}
	
	public byte getByte(int index)
	{
		return mData[index];
	}
	
}
