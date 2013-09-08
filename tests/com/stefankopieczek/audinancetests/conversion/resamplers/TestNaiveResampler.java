package com.stefankopieczek.audinancetests.conversion.resamplers;

import org.junit.Test;

import com.stefankopieczek.audinance.conversion.resamplers.NaiveResampler;
import com.stefankopieczek.audinance.conversion.resamplers.Resampler;

public class TestNaiveResampler extends TestAbstractResampler
{
	private Resampler mResampler = null;	
	
	protected Resampler getResampler()
	{
		if (mResampler == null)
			mResampler = new NaiveResampler();
		
		return mResampler;
	}
}
