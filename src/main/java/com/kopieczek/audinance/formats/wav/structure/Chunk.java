package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.formats.wav.WavDecoder;
import com.kopieczek.audinance.utils.BitUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class Chunk
{
    protected EncodedSource source;
    private Integer mStartIdx;
    private Integer mEndIdx;
    private Integer mLength;

    protected abstract int getChunkSizeOffset();

    public Chunk(EncodedSource source, int startIdx) throws InvalidWavDataException
    {
        this.source = source;
        mStartIdx = startIdx;
    }

    public abstract ByteOrder getEndianism() throws InvalidWavDataException;

    public int getStartIndex()
    {
        return mStartIdx.intValue();
    }

    public int getEndIndex() throws InvalidWavDataException
    {
        if (mEndIdx == null)
            mEndIdx = mStartIdx + getLength();

        return mEndIdx.intValue();
    }

    public int getLength() throws InvalidWavDataException
    {
        int chunkSizeIdx = mStartIdx + getChunkSizeOffset();

        if (mLength == null)
        {
            int lengthValue = BitUtils.intFromBytes(
                                       getRange(chunkSizeIdx, 4), getEndianism());
             mLength = getChunkSizeOffset() + 4 + lengthValue;
        }

        return mLength.intValue();
    }

    protected short getShort(int idx) throws InvalidWavDataException
    {
        byte[] bytes = getRange(getStartIndex() + idx, 2);
        return BitUtils.shortFromBytes(bytes, getEndianism());
    }

    protected int getInt(int idx) throws InvalidWavDataException
    {
        byte[] bytes = getRange(getStartIndex() + idx, 4);
        return BitUtils.intFromBytes(bytes, getEndianism());
    }

    protected String getString(int idx, int length) throws InvalidWavDataException
    {
        byte[] bytes = getRange(getStartIndex() + idx, length);
        return Charset.forName("ASCII").decode(ByteBuffer.wrap(bytes)).toString();
    }

    protected byte[] getRange(int start, int length) {
        return source.getRange(start, length);
    }
}
