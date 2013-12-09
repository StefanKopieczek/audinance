package com.stefankopieczek.audinancetests.testutils;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.audiosources.NoMoreDataException;

/**
 * Mock for DecodedSource which is constructed around an array in order to
 * facilitate testing with known data.
 * 
 * @author Stefan Kopieczek 
 */
public class MockDecodedSource extends DecodedSource
{
	private final double[] mBaseData;
	
	public MockDecodedSource(double[] baseData)
	{
		mBaseData = baseData;
	}

	public double getSample(int idx) throws NoMoreDataException
	{
		if (idx < mBaseData.length)
			return mBaseData[idx];
		else
			throw new NoMoreDataException();
	}
	
	public int getNumSamples()
	{
		return mBaseData.length;
	}
}
