package com.stefankopieczek.audinance;
import java.io.*;

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
		
		while ((position = is.read(buffer, 0, buffer.length)) != 1)
		{
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
}
