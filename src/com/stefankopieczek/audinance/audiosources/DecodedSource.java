package com.stefankopieczek.audinance.audiosources;

public abstract class DecodedSource
{
	public abstract double getSample(int idx) throws NoMoreDataException;
	
	@Override
	public boolean equals(Object o)
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
}
