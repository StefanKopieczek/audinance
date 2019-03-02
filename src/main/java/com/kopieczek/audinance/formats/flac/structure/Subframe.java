package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;

import java.nio.ByteOrder;
import java.util.logging.Logger;

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
    private static final Logger sLogger = Logger.getLogger(Subframe.class.getName());

    protected static final int SUBFRAME_TYPE_IDX = 1;
    protected static final int WASTED_BITS_IDX = 7;
    protected static final int SUBFRAME_TYPE_LENGTH = 6;

    protected final EncodedSource mSrc;
    protected final Frame mParent;

    private Integer mWastedBitsPerSample;
	
	private Integer mWastedBitsFieldLength;	
	
    public Subframe(EncodedSource src, Frame parent)
    {
        // Constructor builds the header fields, except for the typecode which is implied by the specific subclass of
        // Subframe being constructed (e.g. ConstantSubframe has a typecode of 0).
        // The body will be parsed out of the source by the subclass constructor.
        mSrc = src;
        mParent = parent;

        // The number of wasted bits is encoded as either a 0 (for none wasted);
        // or as a '1' followed by n-1 zeroes and a single 1 (for n wasted).
        mWastedBitsPerSample = 0;
        if (src.getBit(WASTED_BITS_IDX) == 1)
        {
            int currentIdx = 7;
            int currentVal = 0;
            while (currentVal == 0)
            {
                currentVal = src.getBit(currentIdx);
                mWastedBitsPerSample += 1 - currentVal;
            }
        }

        mWastedBitsFieldLength = mWastedBitsPerSample + 1;
    }

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
		
		return subframe;
	}
	
	protected static int getTypeCode(EncodedSource src)
	{
	    return src.intFromBits(SUBFRAME_TYPE_IDX, SUBFRAME_TYPE_LENGTH, ByteOrder.BIG_ENDIAN);
	}

	protected int getHeaderSize()
    {
        // The header has constant size up until the last field (wasted_bits_per_sample) which is of variable length.
        return WASTED_BITS_IDX + mWastedBitsFieldLength;
    }

	public int getSize()
	{
		return getHeaderSize() + getBodySize();
	}

	public DecodedSource getSamples()
    {
        return new DecodedSource()
        {
            @Override
            public double getSample(int idx) throws NoMoreDataException, InvalidAudioFormatException
            {
                return Subframe.this.getSample(idx);
            }

            @Override
            public int getNumSamples()
            {
                return mParent.getBlockSize();
            }
        };
    }
	
	public abstract int getBodySize();

    protected abstract double getSample(int idx);
}