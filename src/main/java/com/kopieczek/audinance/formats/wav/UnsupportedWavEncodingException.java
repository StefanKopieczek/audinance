package com.kopieczek.audinance.formats.wav;

import com.kopieczek.audinance.formats.UnsupportedFormatException;

/**
 * Indicates that the wav data is compressed using an unsupported codec; or 
 * is in some other way unable to be decoded by this library, despite being valid.
 */
public class UnsupportedWavEncodingException extends UnsupportedFormatException
{
	public UnsupportedWavEncodingException()
	{
		super();
	}
	
	public UnsupportedWavEncodingException(String message)
	{
		super(message);
	}
	
	public UnsupportedWavEncodingException(WavEncodingType f)
	{
		super("Unsupported wav encoding: " + f);
	}
}
