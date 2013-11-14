package com.stefankopieczek.audinance.formats;

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
