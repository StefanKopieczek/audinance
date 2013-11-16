package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;

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
