package com.stefankopieczek.audinancetests.conversion.multiplexers;

import com.stefankopieczek.audinance.conversion.multiplexers.*;
import com.stefankopieczek.audinance.audiosources.*;
import com.stefankopieczek.audinance.formats.*;
import com.stefankopieczek.audinancetests.testutils.*;

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
}
