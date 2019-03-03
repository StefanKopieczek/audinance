package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.formats.wav.UnsupportedWavEncodingException;
import com.kopieczek.audinance.formats.wav.WavEncodingType;
import com.kopieczek.audinance.formats.wav.structure.Chunk;
import com.kopieczek.audinance.formats.wav.structure.RiffChunk;

import java.nio.ByteOrder;

public class FmtSubchunk extends Chunk
{
    private static final int CHUNK_SIZE_IDX_OFFSET = 4;
    private static final int FORMAT_CODE_IDX_OFFSET = 8;
    private static final int NUM_CHANNELS_IDX_OFFSET = 10;
    private static final int SAMPLE_RATE_IDX_OFFSET = 12;
    private static final int BYTE_RATE_IDX_OFFSET = 16;
    private static final int BLOCK_ALIGN_IDX_OFFSET = 20;
    private static final int BITS_PER_SAMPLE_IDX_OFFSET = 22;
    private static final int EXTRA_PARAMS_SIZE_IDX_OFFSET = 24;
    private static final int EXTRA_PARAMS_IDX_OFFSET = 26;

    private RiffChunk mParent;
    private Short mFormatCode;
    private Short mNumChannels;
    private Integer mSampleRate;
    private Integer mByteRate;
    private Short mBlockAlign;
    private Short mBitsPerSample;
    private Integer mExtraParamsSize;

    public FmtSubchunk(EncodedSource source, int startIdx, RiffChunk parent)
        throws InvalidWavDataException
    {
        super(source, startIdx);
        mParent = parent;
    }

    public ByteOrder getEndianism() throws InvalidWavDataException
    {
        return mParent.getEndianism();
    }

    protected int getChunkSizeIdxOffset()
    {
        return CHUNK_SIZE_IDX_OFFSET;
    }

    public short getFormatCode() throws InvalidWavDataException
    {
        if (mFormatCode == null)
        {
            mFormatCode = getShort(FORMAT_CODE_IDX_OFFSET);
        }

        return mFormatCode.shortValue();
    }

    public WavEncodingType getEncodingType()
        throws UnsupportedWavEncodingException, InvalidWavDataException
    {
        return WavEncodingType.getEncodingTypeFromCode(getFormatCode());
    }

    public short getNumChannels() throws InvalidWavDataException
    {
        if (mNumChannels == null)
        {
            mNumChannels = getShort(NUM_CHANNELS_IDX_OFFSET);
        }

        return mNumChannels.shortValue();
    }

    public int getSampleRate() throws InvalidWavDataException
    {
        if (mSampleRate == null)
        {
            mSampleRate = getInt(SAMPLE_RATE_IDX_OFFSET);
        }

        return mSampleRate.intValue();
    }

    public int getByteRate() throws InvalidWavDataException
    {
        if (mByteRate == null)
        {
            mByteRate = getInt(BYTE_RATE_IDX_OFFSET);
        }

        return mByteRate.intValue();
    }

    public short getBlockAlign() throws InvalidWavDataException
    {
        if (mBlockAlign == null)
        {
            mBlockAlign = getShort(BLOCK_ALIGN_IDX_OFFSET);
        }

        return mBlockAlign.shortValue();
    }

    public short getBitsPerSample() throws InvalidWavDataException
    {
        if (mBitsPerSample == null)
        {
            mBitsPerSample = getShort(BITS_PER_SAMPLE_IDX_OFFSET);
        }

        return mBitsPerSample.shortValue();
    }
}
