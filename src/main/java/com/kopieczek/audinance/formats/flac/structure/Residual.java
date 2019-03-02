package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;

import java.nio.ByteOrder;

public abstract class Residual 
{	
	public abstract int getSize();
	
	public abstract int getCorrection(int idx);
	
	public static Residual buildFromSource(EncodedSource src, 
			                               Frame parentFrame,
			                               int predictorOrder)
	{
		int typeCode = src.intFromBits(0,  2, ByteOrder.BIG_ENDIAN);
		
		Residual residual = null;
		if (typeCode == 0)
		{
			residual = new RiceResidual(src.bitSlice(2), 
					                    parentFrame, 
					                    predictorOrder, 
					                    4);
		}
		else if (typeCode == 1)
		{
			residual = new RiceResidual(src.bitSlice(2), 
										parentFrame,
										predictorOrder,
										5);
		}
		else
		{
			// TODO: Error (reserved block)
		}
		
		return residual;
	}
}