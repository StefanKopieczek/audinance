package com.stefankopieczek.audinance.audiosources;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction over an audio data source. Allows the implementation
 * to decide the best way to access the underlying data.
 */
public abstract class EncodedSource
{
 	/**
	 * Read a byte from the data source at the given index.
	 **/
	public abstract byte getByte(int index) throws NoMoreDataException;
	
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
					result = 0xff & datum;
					mIdx++;
				}
				catch (NoMoreDataException e) {}
				
				return result;
			}
			
		};
	}
}
