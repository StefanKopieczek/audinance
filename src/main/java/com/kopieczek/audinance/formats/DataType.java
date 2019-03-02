package com.kopieczek.audinance.formats;

import com.kopieczek.audinance.formats.flac.FlacData;
import com.kopieczek.audinance.formats.wav.WavData;

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
