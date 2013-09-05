package com.stefankopieczek.audinance.conversion.resamplers;
import com.stefankopieczek.audinance.formats.*;

public interface Resampler
{
	public DecodedAudio resample(DecodedAudio result, 
	                             Integer targetSampleRate);
}
