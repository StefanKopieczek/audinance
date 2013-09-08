package com.stefankopieczek.audinancetests.conversion.resamplers;

import org.junit.Assert;
import org.junit.Test;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.conversion.resamplers.NaiveResampler;
import com.stefankopieczek.audinance.conversion.resamplers.Resampler;
import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinancetests.testutils.MockDecodedSource;

public abstract class TestAbstractResampler 
{
	/**
	 * Build a resampler to use for testing.
	 * Subclasses that test specific resampler implementations should override
	 * this in order to provide the correct resampler implementation for 
	 * testing.
	 * 
	 * @return An implementation of Resampler to use for testing.
	 */
	protected abstract Resampler getResampler();
	
	@Test
	public void testConstruct()
	{
		getResampler();
	}
	
	@Test
	public void testIdentity()
	{
		// Build a DecodedAudio out of a test array.
		int sampleRate = 10000;
		double[] baseData = new double[] {-1.0, 4.5, 17.2, 0, 3.9};
		DecodedSource audioSource = new MockDecodedSource(baseData);
		AudioFormat originalFormat = new AudioFormat(sampleRate, 1);
		DecodedAudio originalAudio = new DecodedAudio(originalFormat,
				                                      audioSource);
		
		Resampler resampler = getResampler();
		
		DecodedAudio resampledAudio = resampler.resample(originalAudio, 
				                                         sampleRate);
		
		Assert.assertEquals("Resampling to the same rate changed the audio!",
				            originalAudio,
				            resampledAudio);
	}
	
	@Test
	public void testHalving()
	{
		// Original audio data.
		int sampleRate = 10000;
		double[] baseData = new double[] {-1.0, 4.5, 17.2, 0, 3.9};
		DecodedSource audioSource = new MockDecodedSource(baseData);
		AudioFormat format = new AudioFormat(sampleRate, 1);
		DecodedAudio originalAudio = new DecodedAudio(format,
				                                      audioSource);
		
		// Halved audio data
		sampleRate = 5000;
		baseData = new double[] {-1.0, 17.2, 3.9};
		audioSource = new MockDecodedSource(baseData);
		format = new AudioFormat(sampleRate, 1);
		DecodedAudio halvedAudio = new DecodedAudio(format,
				                                    audioSource);
		
		Resampler resampler = getResampler();
		
		DecodedAudio resampledAudio = resampler.resample(originalAudio, 
				                                         5000);
		
		Assert.assertEquals("Resampling to half speed didn't simply drop " +
		                    " alternate values!",
				            halvedAudio,
				            resampledAudio);
	}
}
