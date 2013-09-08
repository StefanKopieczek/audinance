package com.stefankopieczek.audinancetests.conversion.resamplers;

import org.junit.Assert;
import org.junit.Test;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.audiosources.NoMoreDataException;
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
		
		assertDecodedAudiosEqual(originalAudio, resampledAudio);
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
				                                         sampleRate);
		
		assertDecodedAudiosEqual(halvedAudio, resampledAudio);
	}
	
	protected void assertDecodedAudiosEqual(DecodedAudio a, DecodedAudio b)
	{
		Assert.assertEquals(a.getFormat(), b.getFormat());
		
		DecodedSource[] aChannels = a.getChannels();
		DecodedSource[] bChannels = b.getChannels();
		
		Assert.assertEquals(aChannels.length, bChannels.length);
		
		for (int idx = 0; idx < a.getChannels().length; idx++)
		{
			assertDecodedSourcesEqual(aChannels[idx], bChannels[idx]);
		}
	}
	
	protected void assertDecodedSourcesEqual(DecodedSource a, DecodedSource b)
	{
		int idx = 0;
		
		while (true)
		{
			boolean aHasData = true;
			double aData = 0.0f;
			try
			{
				aData = a.getSample(idx);
			}
			catch (NoMoreDataException e)
			{
				aHasData = false;
			}
					
			boolean bHasData = true;
			double bData = 0.0f;
			try
			{
				bData = b.getSample(idx);
			}
			catch (NoMoreDataException e)
			{
				bHasData = false;
			}

			Assert.assertEquals("The audio sources were of different lengths!",
					            aHasData, 
					            bHasData);			
			
			// Both sources have run out, and the data has been the same so
			// far. Equal.
			if (!aHasData)
				break;
						
			// The two data differ at this index. Not equal.
			Assert.assertEquals("The audio sources differed at position " + idx,
					            aData, 
					            bData, 
					            0.00001f);
			
			idx += 1;
		}
	}
}
