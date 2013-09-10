package com.stefankopieczek.audinance.formats.wav;

import java.util.HashMap;

public enum WavEncodingType 
{	
	PCM(1),
	MULAW(257),
	ALAW(258),
	ADPCM(259);

	private static final HashMap<Integer, WavEncodingType> mFormatCodes = 
            new HashMap<Integer, WavEncodingType>();	
					
	static
	{
		for (WavEncodingType encodingType : WavEncodingType.values())
		{
			mFormatCodes.put(encodingType.mCode, encodingType);
		}
	}
	
	public static WavEncodingType getEncodingTypeFromCode(int code)
		throws UnsupportedWavEncodingException
	{
		WavEncodingType encodingType = mFormatCodes.get(code);
		
		if (encodingType == null)
		{
			throw new UnsupportedWavEncodingException();
		}
			
		return encodingType;
	}
	
	public final int mCode;
	
	private WavEncodingType(int code)
	{
		mCode = code;
	}	
}
