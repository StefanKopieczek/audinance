package com.stefankopieczek.audinance;
import com.stefankopieczek.audinance.wav.*;

/**
 * The formats which we support processing with Audinance.
 * For each format we provide an <tt>AudioProcessor</tt> to handle
 * encoding and decoding.
 */
public enum AudioFormat
{
 	RAW(RawAudioData.class),
	WAV(WavData.class);
	
	Class<? extends AudioData> mAudioData;
	
	AudioFormat(Class<? extends AudioData> audioData)
	{
		mAudioData = audioData;
	}
}
