package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.utils.BitUtils;

import java.nio.ByteOrder;

public class DataSubchunk extends Chunk
{
    private static final int CHUNK_SIZE_IDX_OFFSET = 4;
    private static final int DATA_IDX_OFFSET = 8;

    private RiffChunk mParent;
    private int mBitsPerSample;

    public DataSubchunk(EncodedSource source, int startIdx, RiffChunk parent, int bitsPerSample) throws InvalidWavDataException
    {
        super(source, startIdx);
        mParent = parent;
        mBitsPerSample = bitsPerSample;
    }

    public ByteOrder getEndianism() throws InvalidWavDataException
    {
        return mParent.getEndianism();
    }

    public int getBitsPerSample()
    {
        return mBitsPerSample;
    }

    protected int getChunkSizeIdxOffset()
    {
        return CHUNK_SIZE_IDX_OFFSET;
    }

    public double getSample(int byteIdx)
            throws InvalidWavDataException, NoMoreDataException
    {
        double result;
        long endOfSample = (long)(byteIdx + Math.ceil(mBitsPerSample / 8.0));
        if (endOfSample >= getLength())
        {
            throw new NoMoreDataException();
        }

        if (mBitsPerSample == 8)
        {
            int tempResult = source.getByte(getStartIndex() + byteIdx);
            tempResult &= 0xFF; // Don't treat the byte as signed.
            result = tempResult * 2; // Normalise energy of sample to match 16bitPCM.

        }
        else if (mBitsPerSample == 16)
        {
            byte[] bytes = getRange(getStartIndex() + byteIdx, 2);
            result = BitUtils.shortFromBytes(bytes, getEndianism());
        }
        else if (mBitsPerSample == 32)
        {
            byte[] bytes = getRange(getStartIndex() + byteIdx, 4);
            // Currently assuming 32-bit int; float to come later.
            // Divide by 65536 = 2^16 to normalise to same energy as 16 bit.
            result = BitUtils.intFromBytes(bytes, getEndianism()) / 65536.0;
        }
        else
        {
            throw new InvalidWavDataException("Unsupported bit depth: " +
                                                              mBitsPerSample);
        }

        return result;
    }
}
