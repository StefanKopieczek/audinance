package com.stefankopieczek.audinance.resampler;
import com.stefankopieczek.audinance.*;

public interface Resampler
{

	public DecodedAudio resample(DecodedAudio result, Integer targetSampleRate);

}
