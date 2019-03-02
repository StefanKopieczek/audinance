package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.formats.DataType;
import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;

/**
 * Indicates that the WAV data we are trying to read is wrongly structured,
 * has inconsistent format information, or is otherwise invalid or corrupt.
 */
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
