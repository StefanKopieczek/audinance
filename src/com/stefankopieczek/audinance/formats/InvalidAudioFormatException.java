package com.stefankopieczek.audinance.formats;

/**
 * Exception indicating that audio provided was in either an invalid
 * or an unexpected format.
 */
public class InvalidAudioFormatException extends Exception
{
	public InvalidAudioFormatException()
	{
		super();
	}
	
	public InvalidAudioFormatException(String message)
	{
		super(message);
	}
	
	public InvalidAudioFormatException(DataType expected)
	{
		super("Expected " + expected.name());
	}
}
