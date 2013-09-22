package com.stefankopieczek.audinance.utils;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility class for Java bits and pieces that don't belong anywhere
 * else.
 *
 * @author Stefan Kopieczek
 */
public class AudinanceUtils
{
	public static final int BYTE_BUFFER_SIZE = 1024;
	
	/**
	 * Reads an <tt>InputStream</tt> into a byte array, and returns
	 * it.
	 *
	 * @param is The InputStream to read into a byte array.
	 * @return The contents of the stream, as a a byte array.
	 */
	public static byte[] getByteArrayFromStream(InputStream is)
		throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BYTE_BUFFER_SIZE];
		int position = 0;
		
		while (true)
		{
			position = is.read(buffer, 0, buffer.length);
			
			if (position == -1)
				break;
			
			baos.write(buffer, 0, position);
		}
		baos.flush();
		
		return baos.toByteArray();
	}
	
	public static boolean nullsafeEquals(Object a, Object b)
	{
		if (a == null ^ b == null)
			return false;
		
		if (a == null && b == null)
			return true;
		
		return a.equals(b);
	}
	
	public static int intFromBytes(byte[] bytes, ByteOrder endianism)
	{
		return ByteBuffer.wrap(bytes).order(endianism).getInt();
	}
	
	public static short shortFromBytes(byte[] bytes, ByteOrder endianism)
	{
		return ByteBuffer.wrap(bytes).order(endianism).getShort();
	}
	
	public static float floatFromBytes(byte[] bytes, ByteOrder endianism)
	{
		return ByteBuffer.wrap(bytes).order(endianism).getFloat();
	}
	
	public static double doubleFromBytes(byte[] bytes, ByteOrder endianism)
	{
		return ByteBuffer.wrap(bytes).order(endianism).getDouble();
	}
	
	public static String stringFromBytes(byte[] bytes)
		throws UnsupportedEncodingException
	{
		return new String(bytes, 0, bytes.length, "ASCII");
	}

}
