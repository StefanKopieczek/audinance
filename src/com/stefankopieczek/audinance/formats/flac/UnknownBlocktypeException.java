package com.stefankopieczek.audinance.formats.flac;

public class UnknownBlocktypeException extends RuntimeException 
{
	public UnknownBlocktypeException(String msg)
	{
		super(msg);
	}
}
