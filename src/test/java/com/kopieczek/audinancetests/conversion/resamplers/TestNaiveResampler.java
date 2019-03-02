package com.kopieczek.audinancetests.conversion.resamplers;

import com.kopieczek.audinancetests.testutils.MockDecodedSource;
import org.junit.Test;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.conversion.resamplers.NaiveResampler;
import com.kopieczek.audinance.conversion.resamplers.Resampler;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;

public class TestNaiveResampler extends AbstractResamplerTest
{
	private Resampler mResampler = null;	
	
	protected Resampler getResampler()
	{
		if (mResampler == null)
			mResampler = new NaiveResampler();
		
		return mResampler;
	}
	
	@Test
	public void testDoubling()
	{
		// Original audio data.
		int sampleRate = 10000;
		double[] baseData = new double[] {-1.0, 4.5, 17.2, 0, 3.9};
		DecodedSource audioSource = new MockDecodedSource(baseData);
		AudioFormat format = new AudioFormat(sampleRate, 1);
		DecodedAudio originalAudio = new DecodedAudio(format,
				                                      audioSource);
		
		// Halved audio data
		sampleRate = 20000;
		baseData = new double[] {-1.0, 1.75, 4.5, 10.85, 17.2, 8.6, 0, 1.95, 3.9};
		audioSource = new MockDecodedSource(baseData);
		format = new AudioFormat(sampleRate, 1);
		DecodedAudio doubledAudio = new DecodedAudio(format,
				                                     audioSource);
		
		Resampler resampler = getResampler();
		
		DecodedAudio resampledAudio = resampler.resample(originalAudio, 
				                                         sampleRate);
		
		assertDecodedAudiosEqual(doubledAudio, resampledAudio);
	}
}
