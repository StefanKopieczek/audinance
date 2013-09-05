package com.stefankopieczek.audinance.formats;

/**
 * The formats which we support processing with Audinance.
 * For each format we provide the corresponding <tt>AudioFile</tt>
 * class which handles handle encoding and decoding.
 */
import com.stefankopieczek.audinance.formats.wav.*;

public enum DataType
{
	WAV(WavData.class);
	
	Class<? extends EncodedAudio> mEncodingType;
	
	DataType(Class<? extends EncodedAudio> encodingType)
	{
		mEncodingType = encodingType;
	}
}
