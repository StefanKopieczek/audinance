package com.stefankopieczek.audinance.formats;

/**
 * Exception indicating that audio provided was in either an invalid
 * or an unexpected format.
 */
public class InvalidAudioFormatException extends RuntimeException
{
	public InvalidAudioFormatException()
	{
		super();
	}
	
	public InvalidAudioFormatException(String message)
	{
		super(message);
	}
	
	public InvalidAudioFormatException(AudioFormat f)
	{
		super("Invalid audio format: " + f);
	}
	
	public InvalidAudioFormatException(Throwable t)
	{		
		super(t);
	}
	
	public InvalidAudioFormatException(String message, Throwable t)
	{
		super(message, t);
	}		
}
