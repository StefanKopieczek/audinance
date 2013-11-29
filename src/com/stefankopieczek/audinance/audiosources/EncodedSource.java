package com.stefankopieczek.audinance.audiosources;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction over an audio data source. Allows the implementation
 * to decide the best way to access the underlying data.
 * The data is accessed byte by byte as it appears in the underlying source,
 * and may contain headers, format information, or any other data depending on
 * the format. No processing is applied to the data at this stage.
 */
public abstract class EncodedSource
{
 	/**
	 * Read a byte from the data source at the given index.
	 **/
	public abstract byte getByte(int index) throws NoMoreDataException;
	
	/**
	 * Returns an input stream which pipes out the data from the source byte
	 * by byte.
	 * 
	 * @return An input stream wrapping the source.
	 */
	public InputStream getInputStream()
	{
		return new InputStream()
		{
			int mIdx = 0;			
			@Override
			public int read() throws IOException 
			{				
				int result = -1;
				try
				{
					byte datum = getByte(mIdx);
					result = 0xff & datum; // Treat as unsigned byte.
					mIdx++;
				}
				catch (NoMoreDataException e) {}
				
				return result;
			}
			
		};
	}
	
	public EncodedSource getTail(final int start)
	{
		final EncodedSource parent = this;
		
		return new EncodedSource()
		{
			@Override
			public byte getByte(int index)
			{
				return parent.getByte(start + index);
			}
		}
	}
	
	public byte getBit(int idx)
	{
		byte octet = idx / 8;
		int offset = idx % 8;
		
		return (octet & (2 ^ offset));
	}
	
	public int intFromBits(int start, int length, ByteOrder order)
	{
		int result = 0;
		
		int byteIdx = start / 8;
		int current = getByte(startByteIdx) & 0xff;
		int bitmask = 2 ^ (start % 8);
		
		int bitWeight = (order == ByteOrder.LITTLE_ENDIAN) ?
			1 :
			2 ^ length;
		
		for (int bitsRead = 0; bitsRead < length; bitsRead++)
		{
			result += current & bitmask;
			bitmask *= 2;
			
			if (bitmask == 64 && bitsRead < length)
			{
				bitmask = 1;
				byteIdx += 1;
				current = getByte(startByteIdx) & 0xff;
			}
		}
		
		return result;
	}
	
	public byte[] toArray()
	{
		return null; //todo
	}
}
