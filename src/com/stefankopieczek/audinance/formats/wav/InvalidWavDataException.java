package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.formats.DataType;
import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;

public class InvalidWavDataException extends InvalidAudioFormatException
{
	public InvalidWavDataException()
	{
		super();
	}
	
	public InvalidWavDataException(String message)
	{
		super(message);
	}
	
	public InvalidWavDataException(Throwable t)
	{
		super (t);
	}
	
	public InvalidWavDataException(String message, Throwable t)
	{
		super(message, t);
	}
}
