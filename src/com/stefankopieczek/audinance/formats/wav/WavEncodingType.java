package com.stefankopieczek.audinance.formats.wav;

import java.util.HashMap;

public enum WavEncodingType 
{	
	PCM(1, "Linear PCM"),
	MULAW(257, "µ-law"),
	ALAW(258, "a-law"),
	ADPCM(259, "Adaptive Differential PCM");

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
	private final String mDisplayName;
	
	private WavEncodingType(int code, String displayName)
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
