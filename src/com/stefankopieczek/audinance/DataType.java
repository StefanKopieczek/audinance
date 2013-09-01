package com.stefankopieczek.audinance;
import com.stefankopieczek.audinance.wav.*;

/**
 * The formats which we support processing with Audinance.
 * For each format we provide the corresponding <tt>AudioFile</tt>
 * class which handles handle encoding and decoding.
 */
public enum DataType
{
	WAV(WavData.class);
	
	Class<? extends AudioData> mAudioData;
	
	DataType(Class<? extends AudioData> audioData)
	{
		mAudioData = audioData;
	}
}
