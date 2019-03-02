package com.kopieczek.audinance.formats;

/**
 * Indicates that we are either unable to read or write in the required
 * format; for example, data we are trying to decode may be in a known
 * datatype, but compressed with an unsupported codec.
 */
public class UnsupportedFormatException extends RuntimeException 
{
	public UnsupportedFormatException()
	{
		super();
	}
	
	public UnsupportedFormatException(String message)
	{
		super(message);
	}
}
