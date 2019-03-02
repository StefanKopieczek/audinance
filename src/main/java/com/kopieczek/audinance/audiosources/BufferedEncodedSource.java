package com.kopieczek.audinance.audiosources;

import java.io.InputStream;

// SMK: The plan is to provide a half-way house between SimpleEncodedSource,
// which simply buffers everything into an array; and just streaming the
// data and forbidding seeking. Not sure what the implementation will be yet.

public class BufferedEncodedSource extends EncodedSource
{
	public BufferedEncodedSource(InputStream is)
	{
		// TODO
	}
	
	public byte getByte(int index) throws NoMoreDataException
	{
		// TODO
		throw new NoMoreDataException();
	}
	
	public int getLength()
	{
		return 0; // TODO
	}
}
