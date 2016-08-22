package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

/**
 * [Description of what a Subframe is]
 * 
 * STRUCTURE:
 * START	END		DESCRIPTION
 * 0		1		Zero bit padding to prevent sync-fooling.
 * 1		6		Subframe type code.
 * 6		7+k		"Wasted bits per sample" flag: Either 0, or 1 followed by
 *                  a unary-encoded number (e.g. 1 followed by 001 for 3).
 * 7+k      ?       Subframe body.		
 * 
 * @author Stefan Kopieczek
 *
 */
public abstract class Subframe
{		
	private Integer mWastedBitsPerSample;
	
	private Integer mWastedBitsFieldLength;	
	
	public static Subframe buildFromSource(EncodedSource src, Frame parent)
	{
		Subframe subframe = null;
		int typeCode = getTypeCode(src);
		if (typeCode == 0)
		{
			subframe = new ConstantSubframe(src, parent);
		}
		else if (typeCode == 1)
		{
			subframe = new VerbatimSubframe(src, parent);
		}
		else if (typeCode < 8)
		{
			// Error (reserved block): TODO			
		}
		else if (typeCode < 16)
		{
			// Type codes in the range [8,16) indicate that the subframe is 
			// based on a fixed linear predictor. 
			// The last three bits of the type code indicate the order of
			// predictor used.
			int predictorOrder = typeCode & 0b000111;
			if (predictorOrder <= 4)
			{
				subframe = new FixedPredictorSubframe(src, parent, predictorOrder);
			}
			else
			{
				// TODO error: Only orders 0-4 are permitted.
			}
		}
		else if (typeCode < 32)
		{
			// Error (reserved block): TODO
		}
		else
		{
			// Type codes greater than 32 indicate that the subframe is based
			// on an LPC predictor. The last five bits indicate the order of
			// predictor used (minus one).
			int predictorOrder = (typeCode & 0b011111) + 1;
			subframe = new LpcSubframe(src, parent, predictorOrder);
		}
		 
		// The number of wasted bits is encoded as either a 0 (for none wasted);
		// or as a '1' followed by n-1 zeroes and a single 1 (for n wasted).
		int wastedBits = 0;
		if (src.getBit(6) == 1)
		{
			int currentIdx = 7;
			int currentVal = 0;
			while (currentVal == 0)
			{
				currentVal = src.getBit(currentIdx);
				wastedBits += 1 - currentVal;
			}
		}
		
		subframe.setWastedBitsPerSample(wastedBits);
		
		return subframe;
	}
	
	public static int getTypeCode(EncodedSource src)
	{
		return 0; // TODO
	}
	
	public int getSize()
	{
		return 7 + getWastedBitFieldLength() + getBodySize();
	}
	
	private int getWastedBitFieldLength()
	{
		if (mWastedBitsFieldLength == null)			
		{
			getWastedBitsPerSample();
		}
		
		return mWastedBitsFieldLength;
	}
	
	public int getWastedBitsPerSample()
	{
		return mWastedBitsPerSample;
	}	
	
	public void setWastedBitsPerSample(int wastedBits)
	{
		mWastedBitsPerSample = wastedBits;
		mWastedBitsFieldLength = wastedBits + 1;
	}
	
	public abstract int getBodySize();
	
	public abstract double getSample(int idx);
}