package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

import java.util.*;

public abstract class PredictiveSubframe extends Subframe 
{			
	private SampleCache mSampleCache;
	
	@Override
	public double getSample(int idx)
	{
	    return getPredictedSample(idx) + getResidual().getCorrection(idx);
	}
	
	protected abstract int[] getWarmUpSamples();
	
	protected abstract int getOrder();
	
	protected abstract int[] getCoefficients();		

	protected abstract Residual getResidual();

	public PredictiveSubframe(EncodedSource src, Frame parent)
    {
        super(src, parent);
    }

	private void initSampleCache()
	{
		int[] warmUpSamples = getWarmUpSamples();
		HashMap<Integer, Integer> indicesWithSamples = new HashMap<Integer, Integer>();
		
		for (int ii = 0; ii < getOrder(); ii++)
		{
			indicesWithSamples.put(ii, warmUpSamples[ii]);
		}
		
		mSampleCache = new SampleCache(indicesWithSamples, getOrder());
	}
	
	private int evaluate(List<Integer> samples)
	{
		int[] coefficients = getCoefficients();
		assert(samples.size() == coefficients.length);
		
		int total = 0;
		for (int ii = 0; ii < samples.size(); ii++)
		{
			total += samples.get(ii) * coefficients[ii];
		}
		
		return total;
	}
	
	public double getPredictedSample(int idx)
	{		
		if (mSampleCache.hasSample(idx))
		{
			return mSampleCache.getSample(idx);
		}
		
		boolean samplesInCache = true;
		for (int ii = idx - getOrder(); ii < idx; ii++)
		{
			samplesInCache = samplesInCache && mSampleCache.hasSample(ii);
			if (!samplesInCache)
				break;
		}
		
		int result;
		if (!samplesInCache)
		{
			// Get the predicted sample by calculating from the start of the
			// subframe.
			ArrayList<Integer> currentSamples = new ArrayList<Integer>();
			for (int sample : getWarmUpSamples())
			{
				currentSamples.add(sample);				
			}			
						
			for (int currentIdx = getOrder(); currentIdx <= idx; currentIdx++)
			{
				currentSamples.add(evaluate(currentSamples));
				currentSamples.remove(0); // TODO - Efficient?
			}		
			
			// Cache the newly-calculated samples.
			// TODO: Tidy this the hell up.
			for (int ii = getOrder() - 1; ii >= 0; ii--)
			{
				mSampleCache.cacheValue(idx - ii, currentSamples.get(getOrder() - ii + 1));
			}
			
			result = mSampleCache.getSample(getOrder() - 1);
		}
		else
		{
			ArrayList<Integer> previousSamples = new ArrayList<Integer>();			
			for (int ii = idx - getOrder(); ii < idx; ii++)
			{
				previousSamples.add(mSampleCache.getSample(ii));				
			}	
			
			result = evaluate(previousSamples);
		}
		
		// TODO This will never work first time...
		return result;
	}
	
	/**
	 * Cache used to make calculation of successive samples more efficient
	 * (naive implementation would have to recalculate all previous samples
	 * each time).
	 * @author Stefan Kopieczek
	 *
	 */
	private static class SampleCache
	{
		private final HashMap<Integer, Integer> mFixedValues;
		private final HashMap<Integer, Integer> mCache;
		private final Queue<Integer> mRecentKeys;
		private final int mCacheSize;
		
		public SampleCache(HashMap<Integer, Integer> fixedValues, int cacheSize)
		{
			mFixedValues = fixedValues;
			mCache = new HashMap<Integer, Integer>();
			mRecentKeys = new ArrayDeque<Integer>();
			mCacheSize = cacheSize;
		}
		
		public void cacheValue(int idx, int sample)
		{
			if (mCache.containsKey(idx))
			{
				// We already have that sample, so just put it to the back of
				// the recent keys queue to stop it expiring early.
				mRecentKeys.remove(idx);
				mRecentKeys.add(idx);
			}
			else
			{
				// Add the sample to the cache, and expire the oldest entry 
				// from the recent keys queue.
				mCache.put(idx, sample);
				mRecentKeys.add(sample);
				
				if (mRecentKeys.size() > mCacheSize)
				{
					Integer topKey = mRecentKeys.remove();
					mCache.remove(topKey);
				}
			}
		}
		
		public boolean hasSample(long idx)
		{
			assert(idx < Integer.MAX_VALUE);
			return mFixedValues.containsKey((int)idx) || mCache.containsKey((int)idx);
		}
		
		public int getSample(int idx)
		{
			Integer result;
			
			if (mFixedValues.containsKey(idx))
			{
				result = mFixedValues.get(idx);
			}
			else if (mCache.containsKey(idx))
			{
				result = mFixedValues.get(idx);
			}
			else
			{
				throw new RuntimeException("Sample not found: " + idx); // TODO
			}
			
			return result.intValue();
		}
	}
}
