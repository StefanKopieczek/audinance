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
}
