package com.kopieczek.audinance.testutils;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class TestUtilities
{
	public static void assertDecodedAudioEqual(String message,
	                                           DecodedAudio expected,
	                                           DecodedAudio actual)
	{		
		assertFormatsEqual(message + " audio formats are not equal",
		                   expected.getFormat(),
		                   actual.getFormat());
						   
		DecodedSource[] expectedChannels = expected.getChannels();
		DecodedSource[] actualChannels = actual.getChannels();
		
		double[][] expectedData = 
						        new double[expectedChannels.length][];
		for (int ii = 0; ii < expectedChannels.length; ii++)
		{
			try
			{
				expectedData[ii] = getRawDataFromDecodedSource(
			                                    	expectedChannels[ii]);
			}
			catch (InvalidAudioFormatException e)
			{
				Assert.fail("Expected data invalid in channel  " + ii);
			}
		}
		
		double[][] actualData = 
		                          new double[actualChannels.length][];
		for (int ii = 0; ii < actualChannels.length; ii++)
		{
			try
			{
				actualData[ii] = getRawDataFromDecodedSource(
			                 	                  	actualChannels[ii]);
			}
			catch (InvalidAudioFormatException e)
			{
				Assert.fail("Actual data invalid in channel  " + ii);
			}
		}
		
		Assert.assertArrayEquals(message + "Audio data not equal.", 
		                         expectedData, 
								 actualData);
	}
	
	private static double[] getRawDataFromDecodedSource(
											     DecodedSource source)
		throws InvalidAudioFormatException
	{
		List<Double> rawData = new ArrayList<Double>();
		
		int ii = 0;
		while (true)
		{
			try
			{
				rawData.add(source.getSample(ii));
			}
			catch (NoMoreDataException e)
			{
				break;
			}
			
			ii++;
		}
		
		double[] result = new double[rawData.size()];
		
		for (int jj = 0; jj < rawData.size(); jj++)
		{
			result[ii] = rawData.get(ii).doubleValue();
		}
		
		return result;
	}
	
	private static void assertFormatsEqual(String message,
	                                AudioFormat expected,
									AudioFormat actual)
	{
		Assert.assertEquals(message + " Sample rates unequal.", 
		                    expected.getSampleRate(),
							actual.getSampleRate());
							
		Assert.assertEquals(message + " Number of channels differs.",
		                    expected.getSampleRate(),
							actual.getSampleRate());
							
		// Catch changes in underlying equals() method not
		// updated here.
		Assert.assertEquals(message, expected, actual);
	}
}
