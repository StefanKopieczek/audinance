package com.kopieczek.audinance.formats.wav.structure;

import com.google.common.base.Suppliers;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.formats.wav.UnsupportedWavEncodingException;
import com.kopieczek.audinance.formats.wav.WavEncodingType;

import java.nio.ByteOrder;
import java.util.function.Supplier;

public class FmtSubchunk extends Chunk {
    private static final int CHUNK_SIZE_OFFSET = 4;
    private static final int FORMAT_CODE_OFFSET = 8;
    private static final int NUM_CHANNELS_OFFSET = 10;
    private static final int SAMPLE_RATE_OFFSET = 12;
    private static final int BYTE_RATE_OFFSET = 16;
    private static final int BLOCK_ALIGN_OFFSET = 20;
    private static final int BITS_PER_SAMPLE_OFFSET = 22;

    private final Supplier<Short> formatCode = Suppliers.memoize(() -> getShort(FORMAT_CODE_OFFSET));
    private final Supplier<Short> numChannels = Suppliers.memoize(() -> getShort(NUM_CHANNELS_OFFSET));
    private final Supplier<Integer> sampleRate = Suppliers.memoize(() -> getInt(SAMPLE_RATE_OFFSET));
    private final Supplier<Integer> byteRate = Suppliers.memoize(() -> getInt(BYTE_RATE_OFFSET));
    private final Supplier<Short> blockAlign = Suppliers.memoize(() -> getShort(BLOCK_ALIGN_OFFSET));
    private final Supplier<Short> bitsPerSample = Suppliers.memoize(() -> getShort(BITS_PER_SAMPLE_OFFSET));

    private RiffChunk parent;

    public FmtSubchunk(EncodedSource source, int startIdx, RiffChunk parent) throws InvalidWavDataException {
        super(source, startIdx);
        this.parent = parent;
    }

    @Override
    public ByteOrder getEndianism() throws InvalidWavDataException {
        return parent.getEndianism();
    }

    @Override
    protected int getChunkSizeOffset()
    {
        return CHUNK_SIZE_OFFSET;
    }

    public short getFormatCode() {
        return formatCode.get();
    }

    public WavEncodingType getEncodingType() throws UnsupportedWavEncodingException, InvalidWavDataException {
        return WavEncodingType.getEncodingTypeFromCode(getFormatCode());
    }

    public short getNumChannels() throws InvalidWavDataException {
        return numChannels.get();
    }

    public int getSampleRate() throws InvalidWavDataException {
        return sampleRate.get();
    }

    public int getByteRate() throws InvalidWavDataException {
        return byteRate.get();
    }

    public short getBlockAlign() throws InvalidWavDataException {
        return blockAlign.get();
    }

    public short getBitsPerSample() throws InvalidWavDataException {
        return bitsPerSample.get();
    }
}
