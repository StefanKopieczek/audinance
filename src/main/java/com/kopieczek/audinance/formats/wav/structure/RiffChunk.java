package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.utils.BitUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

public class RiffChunk extends Chunk
{
    public static final int ID_IDX_OFFSET = 0;
    public static final int CHUNK_SIZE_IDX_OFFSET = 4;
    public static final int DATA_IDX_OFFSET = 12;

    private ByteOrder mEndianism;

    public RiffChunk(EncodedSource source, int startIdx) throws InvalidWavDataException
    {
        super(source, startIdx);
    }

    public ByteOrder getEndianism() throws InvalidWavDataException
    {
        if (mEndianism == null)
        {
            String chunkId = null;
            try
            {
                chunkId = BitUtils.stringFromBytes(getRange(ID_IDX_OFFSET, 4));
            }
            catch (UnsupportedEncodingException e)
            {
                throw new InvalidWavDataException("Invalid RIFF header ID format.",
                                                    e);
            }

            if (chunkId.equals("RIFF"))
            {
                mEndianism = ByteOrder.LITTLE_ENDIAN;
            }
            else if (chunkId.equals("RIFX"))
            {
                mEndianism = ByteOrder.BIG_ENDIAN;
            }
            else
            {
                throw new InvalidWavDataException("Invalid RIFF header ID " +
                                                  chunkId);
            }
        }

        return mEndianism;
    }

    protected int getChunkSizeIdxOffset()
    {
        return CHUNK_SIZE_IDX_OFFSET;
    }
}
