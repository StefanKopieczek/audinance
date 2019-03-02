package com.kopieczek.audinance.conversion.multiplexers;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.testutils.MockDecodedSource;
import com.kopieczek.audinance.testutils.TestUtilities;
import org.junit.Test;

public abstract class AbstractMultiplexerTest
{
	protected abstract Multiplexer getMultiplexer();
	
	protected void assertOutputAsExpected(String message, double[][] input, int numChannels, double[][] expectedOutput) {
		AudioFormat dummyFormat = new AudioFormat(null, input.length);

		DecodedSource[] inputChannels = new DecodedSource[input.length];
		for (int idx = 0; idx < input.length; idx++) {
			inputChannels[idx] = new MockDecodedSource(input[idx]);
		}

		DecodedSource[] expectedOutputChannels = new DecodedSource[expectedOutput.length];
		for (int idx = 0; idx < expectedOutput.length; idx++) {
			expectedOutputChannels[idx] = new MockDecodedSource(expectedOutput[idx]);
		}

		DecodedAudio inputAudio = new DecodedAudio(dummyFormat, inputChannels);
		DecodedAudio expectedOutputAudio = new DecodedAudio(dummyFormat, expectedOutputChannels);

		Multiplexer multiplexer = getMultiplexer();
		DecodedAudio actualOutputAudio = multiplexer.toNChannels(inputAudio, numChannels);
														
		TestUtilities.assertDecodedAudioEqual(message, expectedOutputAudio, actualOutputAudio);
	}
	
	@Test
	public void testSingleChannelIdentity() {
		double[] testChannel = {47.2, -7, 0.3, 9.8};
		double[][] testData = {testChannel};
		
		assertOutputAsExpected("One-channel data plexed to one channel did not remain the same!",
							   testData,
							   1,
							   testData);
	}
	
	@Test
	public void testMultiChannelIdentity() {
		int numChannels = 3;
		double[][] testData = new double[numChannels][];
		testData[0] = new double[]{4.4, 6.0, 0, 0.4, -3, 7};
		testData[1] = new double[]{5.58, -9.99, -2, -7.3, 9.0, 5.9};
		testData[2] = new double[]{-9, 3.2, 4.743, 5.8};
		
		assertOutputAsExpected("Multi-channel data plexed to the same number of channels did not remain the same!",
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
