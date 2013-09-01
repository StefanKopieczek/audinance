package com.stefankopieczek.audinance;

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
