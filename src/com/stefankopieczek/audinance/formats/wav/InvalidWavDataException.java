package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.formats.DataType;
import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;

public class InvalidWavDataException extends Exception
{
	public InvalidWavDataException()
	{
		super();
	}
	
	public InvalidWavDataException(String message)
	{
		super(message);
	}
}
