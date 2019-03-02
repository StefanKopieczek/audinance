package com.kopieczek.audinance.formats.wav;

import java.util.HashMap;

public enum WavEncodingType 
{	
	PCM  ((short)1,   "Linear PCM"),
	MULAW((short)257, "�-law"),
	ALAW ((short)258, "a-law"),
	ADPCM((short)259, "Adaptive Differential PCM");

	private static final HashMap<Short, WavEncodingType> mFormatCodes = 
            new HashMap<Short, WavEncodingType>();	
					
	static
	{
		for (WavEncodingType encodingType : WavEncodingType.values())
		{
			mFormatCodes.put(encodingType.mCode, encodingType);
		}
	}
	
	public static WavEncodingType getEncodingTypeFromCode(short code)
		throws UnsupportedWavEncodingException
	{
		WavEncodingType encodingType = mFormatCodes.get(code);
		
		if (encodingType == null)
		{
			throw new UnsupportedWavEncodingException();
		}
			
		return encodingType;
	}
	
	public final short mCode;
	private final String mDisplayName;
	
	private WavEncodingType(short code, String displayName)
	{
		mCode = code;
		mDisplayName = displayName;
	}
	
	@Override
	public String toString()
	{
		return mDisplayName;
	}
}
