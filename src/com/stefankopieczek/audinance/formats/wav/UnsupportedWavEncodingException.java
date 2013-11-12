package com.stefankopieczek.audinance.formats.wav;

import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;

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
