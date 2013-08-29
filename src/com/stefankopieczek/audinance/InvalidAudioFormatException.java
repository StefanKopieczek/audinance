package com.stefankopieczek.audinance;

public class InvalidAudioFormatException extends Exception
{
	public InvalidAudioFormatException()
	{
		super();
	}
	
	public InvalidAudioFormatException(AudioFormat expected)
	{
		super("Expected " + expected.name());
	}
}
