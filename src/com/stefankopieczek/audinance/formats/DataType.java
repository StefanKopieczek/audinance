package com.stefankopieczek.audinance.formats;

import com.stefankopieczek.audinance.formats.wav.WavData;
import com.stefankopieczek.audinance.formats.flac.FlacData;

/**
 * The formats which we support processing with Audinance.
 * For each format we provide the corresponding <tt>EncodedAudio</tt>
 * class which handles handle encoding and decoding.
 */
public enum DataType
{
	WAV(WavData.class),
	FLAC(FlacData.class);
	
	Class<? extends EncodedAudio> mEncodingType;
	
	DataType(Class<? extends EncodedAudio> encodingType)
	{
		mEncodingType = encodingType;
	}
}
