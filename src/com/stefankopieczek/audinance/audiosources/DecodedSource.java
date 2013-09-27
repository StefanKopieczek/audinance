package com.stefankopieczek.audinance.audiosources;

import java.util.ArrayList;
import java.util.List;

import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;

public abstract class DecodedSource
{
	public abstract double getSample(int idx) 
			throws NoMoreDataException, InvalidAudioFormatException;
	
	@Override
	public boolean equals(Object o)
	{
		try
		{
			if (!(o instanceof DecodedSource))
				return false;
			
			int idx = 0;		
			while (true)
			{
				boolean thisHasData = true;
				double thisData = 0.0f;
				try
				{
					thisData = getSample(idx);
				}
				catch (NoMoreDataException e)
				{
					thisHasData = false;
				}
						
				boolean otherHasData = true;
				double otherData = 0.0f;
				try
				{
					otherData = ((DecodedSource)o).getSample(idx);
				}
				catch (NoMoreDataException e)
				{
					otherHasData = false;
				}
				
				// Precisely one of the sources has run out, so the lengths must be
				// different. Not equal.
				if (thisHasData != otherHasData)
					return false;
				
				// Both sources have run out, and the data has been the same so
				// far. Equal.
				if (!thisHasData)
					return true;
							
				// The two data differ at this index. Not equal.
				if (thisData != otherData)
					return false;
				
				idx += 1;
			}				
		}
		catch (InvalidAudioFormatException e)
		{
			// There's not much we can do if the underlying audio is invalid.
			// We say that invalid audio is never equal to anything; this
			// might be a mistake, but it's hard to see an alternative.
			return false;
		}
	}
	
	public double[] getRawData() throws InvalidAudioFormatException
	{
		List<Double> data = new ArrayList<Double>();
		
		int idx = 0;
		while (true)
		{
			try
			{
				data.add(getSample(idx));
			}
			catch (NoMoreDataException e)
			{
				break;
			}
			
			idx++;
		}
		
		double[] result = new double[data.size()];
		
		for (int ii = 0; ii < data.size(); ii++)
		{
			result[ii] = data.get(ii).doubleValue();
		}
		
		return result;
	}
}
