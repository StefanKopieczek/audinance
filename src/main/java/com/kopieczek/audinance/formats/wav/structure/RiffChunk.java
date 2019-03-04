package com.kopieczek.audinance.formats.wav.structure;

import com.google.common.base.Suppliers;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;

import java.nio.ByteOrder;
import java.util.function.Supplier;

public class RiffChunk extends Chunk {
    public static final int CHUNK_ID_OFFSET_IN_BYTES = 0;
    public static final int CHUNK_SIZE_OFFSET_IN_BYTES = 4;
    public static final int CHUNK_DATA_OFFSET_IN_BYTES = 12;

    private Supplier<ByteOrder> endianism = Suppliers.memoize(this::getEndianismInternal);

    public RiffChunk(EncodedSource source, int startIdx) throws InvalidWavDataException {
        super(source, startIdx);
    }

    public ByteOrder getEndianism() {
        return endianism.get();
    }

    private ByteOrder getEndianismInternal() throws InvalidWavDataException {
        String chunkId = getString(CHUNK_ID_OFFSET_IN_BYTES, CHUNK_SIZE_OFFSET_IN_BYTES);

        switch (chunkId) {
            case "RIFF":
                return ByteOrder.LITTLE_ENDIAN;
            case "RIFX":
                return ByteOrder.BIG_ENDIAN;
            default:
                throw new InvalidWavDataException("Invalid RIFF header ID " + chunkId);
        }
    }

    protected int getChunkSizeOffset() {
        return CHUNK_SIZE_OFFSET_IN_BYTES;
    }
}
