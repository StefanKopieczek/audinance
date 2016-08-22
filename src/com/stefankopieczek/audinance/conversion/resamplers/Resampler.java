package com.stefankopieczek.audinance.conversion.resamplers;

import com.stefankopieczek.audinance.formats.DecodedAudio;

/**
 * Class which takes decoded audio data and outputs new decoded audio at a
 * different sample rate, trying to maintain as much fidelity as possible to
 * the original sound.
 * @author Stefan Kopieczek
 *
 */
public interface Resampler
{
	/**
	 * Takes decoded audio data and returns data that sounds as similar as
	 * possible, but has the specified sample rate. The original data is
	 * unchanged. 
	 * @param originalAudio The data to resample.
	 * @param targetSampleRate The desired sample rate.
	 * @return A copy of the original audio, at the desired rate.
	 */
	public DecodedAudio resample(DecodedAudio originalAudio, 
	                             Integer targetSampleRate);
}
