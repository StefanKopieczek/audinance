package com.stefankopieczek.audinance;
import java.io.*;

public class AudinanceUtils
{
	public static final int BYTE_BUFFER_SIZE = 1024;
	
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
}
