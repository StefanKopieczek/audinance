package com.stefankopieczek.audinancetests.conversion.multiplexers;

import com.stefankopieczek.audinance.audiosources.*;
import com.stefankopieczek.audinance.conversion.multiplexers.*;
import com.stefankopieczek.audinance.formats.*;
import com.stefankopieczek.audinancetests.testutils.*;
import org.junit.*;

public abstract class TestAbstractMultiplexer
{
	protected abstract Multiplexer getMultiplexer();
	
	protected void assertOutputAsExpected(String message,
	                                      double[][] input, 
	                                      int numChannels,
	        				              double[][] expected)
	{
		DecodedSource[] channelsIn = new DecodedSource[input.length];
		DecodedSource[] expectedChannels =
		                           new DecodedSource[expected.length];
		
		for (int idx = 0; idx < input.length; idx++)
		{
			channelsIn[idx] = new MockDecodedSource(input[idx]);
		}
		
		for (int idx = 0; idx < expected.length; idx++)
		{
			expectedChannels[idx] = 
			                     new MockDecodedSource(expected[idx]);
		}
		
		AudioFormat format = new AudioFormat(5000, input.length);
		DecodedAudio audioIn = new DecodedAudio(format, channelsIn);
		DecodedAudio expectedAudio = 
		                   new DecodedAudio(format, expectedChannels);
		
		Multiplexer multiplexer = getMultiplexer();
		DecodedAudio audioOut = multiplexer.toNChannels(audioIn,
		                                                numChannels);
														
		TestUtilities.assertDecodedAudioEqual(message,
		                                      expectedAudio,
											  audioOut);
	}
	
	@Test
	public void testSingleChannelIdentity()
	{
		double[] testChannel = {47.2, -7, 0.3, 9.8};
		double[][] testData = {testChannel};
		
		assertOutputAsExpected("One-channel data plexed to one " +
		                       "channel did not remain the same!",
							   testData,
							   1,
							   testData);
	}
	
	@Test
	public void testMultiChannelIdentity()
	{
		int numChannels = 3;
		double[][] testData = new double[numChannels][];
		testData[0] = new double[]{4.4, 6.0, 0, 0.4, -3, 7};
		testData[1] = new double[]{5.58, -9.99, -2, -7.3, 9.0, 5.9};
		testData[2] = new double[]{-9, 3.2, 4.743, 5.8};
		
		assertOutputAsExpected("Multi-channel data plexed to the " +
		                       "same number of channels did not " +
							   "remain the same!",
							   testData,
							   numChannels,
							   testData);
	}
	
	@Test
	public abstract void testMonoToStereo();
	
	@Test
	public abstract void testStereoToMono();
	
	@Test
	public abstract void plexUpWhenChannelEndsPrematurely();
	
	@Test
	public abstract void plexDownWhenChannelEndsPrematurely();
}
