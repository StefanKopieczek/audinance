package com.stefankopieczek.audinance.audiosources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

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
			
			@Override
			public int getLength()
			{
				return parent.getLength() - start;
			}
		};
	}
	
	public byte getBit(int idx)
	{
		byte octet = getByte(idx / 8);
		int offset = 7 - (idx % 8);
		byte mask = (byte)(Math.pow(2, offset));
		
		return (byte)(((octet & mask) == 0) ? 0 : 1);
	}
	
	public int intFromBits(int start, int length, ByteOrder endianism)
	{
		int result = 0;
		
		int byteIdx = start / 8;
		int current = getByte(byteIdx) & 0xff;		
		int offset = 7 - (start % 8);
		int bitMask = (int)(Math.pow(2, offset));		
		
		// TODO: This really isn't how little endianism works :(
		int bitWeight = (endianism == ByteOrder.LITTLE_ENDIAN) ?
			1 :
			(int)Math.pow(2, length-1);
		
		for (int bitsRead = 0; bitsRead < length; bitsRead++)
		{
			result += (((current & bitMask) == 0) ? 0 : 1) * bitWeight;
			bitMask /= 2;
			
			if (bitMask == 0 && bitsRead < length)
			{
				bitMask = 0b10000000;
				byteIdx += 1;
				current = getByte(byteIdx) & 0xff;
			}
			
			if (endianism == ByteOrder.LITTLE_ENDIAN)
			{
				bitWeight *= 2;
			}
			else
			{
				bitWeight /= 2;
			}
		}
		
		return result;
	}
	
	public byte[] toArray()
	{
		throw new RuntimeException("TODO"); // TODO		
	}

	// TODO: Deduplicate this code somehow!
	public long longFromBits(int start, int length, ByteOrder endianism) 
	{		
		long result = 0;
		
		int byteIdx = start / 8;
		int current = getByte(byteIdx) & 0xff;
		int bitmask = 2 ^ (start % 8);
		
		int bitWeight = (endianism == ByteOrder.LITTLE_ENDIAN) ?
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
				current = getByte(start) & 0xff;
			}
		}
		
		return result;		
	}
	
	public EncodedSource bitSlice(final int start, final int length)
	{
		// To save code, we use a delegate with unlimited length and then
		// perform a length check before we pass calls down to it.
		final EncodedSource delegate = bitSlice(start);
		
		return new EncodedSource()
		{
			@Override
			public byte getByte(int index) throws NoMoreDataException
			{
				if (index * 8 >= length - 7)
				{
					throw new NoMoreDataException();
				}
				else
				{
					return delegate.getByte(index);
				}
			}
			
			@Override
			public byte getBit(int index) throws NoMoreDataException
			{
				if (index >= length)
				{
					throw new NoMoreDataException();
				}
				else
				{
					return delegate.getBit(index);
				}
			}

			@Override
			public int getLength() 
			{
				return delegate.getLength();
			}
			
		};
	}
	
	public EncodedSource bitSlice(final int start)
	{
		final EncodedSource parent = this;
		final boolean isByteMultiple = (start % 8 == 0);
		
		return new EncodedSource()
		{
			@Override
			public byte getByte(int index) throws NoMoreDataException
			{
				byte result = 0;
				
				if (isByteMultiple)
				{
					result = parent.getByte(start + index);
				}
				else
				{
					// The byte we want splits over two bytes from the parent
					// source, because the bit offset of this slice isn't a 
					// multiple of 8.
					// Therefore we need to take parts of both neighbour bytes
					// from the parent and put them together.
					int parentBitIndex = start + index * 8;
					int leftByteIndex = parentBitIndex / 8;
					byte leftByte = parent.getByte(leftByteIndex);
					byte rightByte = parent.getByte(leftByteIndex + 1);
					long leftBitsNeeded = parentBitIndex % 8;
					
					long leftBitsMask = (long)(Math.pow(2, leftBitsNeeded)) - 1;
					result = (byte)((leftByte & leftBitsMask) << (8 - leftBitsNeeded));
					result += (rightByte >> (8 - leftBitsNeeded));
				}		
				
				return result;
			}

			@Override
			public byte getBit(int index) throws NoMoreDataException 
			{
				return parent.getBit(start + index);
			}
			
			@Override
			public int getLength() 
			{
				return parent.getLength() - start;
			}
			
			@Override 
			public EncodedSource bitSlice(final int start2)
			{
				// Override the default bitSlice to call through to the parent
				// so as to prevent inefficiencies when bitSlices are chained.
				return parent.bitSlice(start + start2);
			}
			
			@Override
			public EncodedSource bitSlice(final int start2, final int length)
			{
				// Override the default bitSlice to call through to the parent
				// so as to prevent inefficiencies when bitSlices are chained.
				return parent.bitSlice(start + start2, length);
			}
		};
	}

	public long getUtf8Codepoint(int bitIndex)
	{
	    long result = 0;

        int bytesRemaining = 0;
		if (getBit(bitIndex) == 0)
        {
            result = intFromBits(bitIndex + 1, 7, ByteOrder.BIG_ENDIAN);
        }
        else if (getBit(bitIndex + 2) == 0)
        {
            result = intFromBits(bitIndex + 3, 5, ByteOrder.BIG_ENDIAN);
            bytesRemaining = 1;
        }
        else if (getBit(bitIndex + 3) == 0)
        {
            result = intFromBits(bitIndex + 4, 4, ByteOrder.BIG_ENDIAN);
            bytesRemaining = 2;
        }
        else
        {
            result = intFromBits(bitIndex + 5, 3, ByteOrder.BIG_ENDIAN);
            bytesRemaining = 3;
        }

        while (bytesRemaining > 0)
        {
            bitIndex += 8;
            result *= 128;
            result += intFromBits(bitIndex + 2, 6, ByteOrder.BIG_ENDIAN);
            bytesRemaining -= 1;
        }

        return result;
	}
	
	public abstract int getLength();
}
