package com.stefankopieczek.audinance.formats.flac;

public class InvalidFlacDataException extends RuntimeException 
{
	public InvalidFlacDataException(String msg)
	{
		super(msg);
	}
}
