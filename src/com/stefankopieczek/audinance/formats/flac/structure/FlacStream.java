package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.flac.FlacDecoder;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FlacStream
{
    private static final Logger sLogger = Logger.getLogger(FlacStream.class.getName());
    private static final int METADATA_START_IDX = 32;
	
	private int mFramesStartIdx;
	
	private List<MetadataBlock> mMetadata;

    private EncodedSource mSource;

    // This is a horrible hack and you should feel bad.
    // We do this to prevent having to deal with variable bitrates
    // upstream. Ech.
    public Frame mFirstFrame;
    private boolean mIsFirstFrame = true;
	
	private StreamInfoBlock mStreamInfo;
	
	private Boolean mHasSeektable = null;
	
	private SeektableBlock mSeektableBlock;
    private int mPtr;

    public FlacStream(EncodedSource source) {
        sLogger.fine("Constructing FlacStream for source " + source);

        // Get the metadata
        this.mSource = source;
        mPtr = METADATA_START_IDX;
        boolean moreBlocks = true;
        mMetadata = new ArrayList<MetadataBlock>();

        while (moreBlocks) {
            MetadataBlock next = MetadataBlock.buildFromSource(mSource, mPtr);
            sLogger.fine("Parsed metadata block " + next + "; isLastBlock? " + next.mIsLastBlock);

            mMetadata.add(next);
            mPtr += next.mLength;
            moreBlocks = !next.mIsLastBlock;
        }

        // The first metadatum is always a StreamInfoBlock.
        // Todo: handle failure better here.
        mStreamInfo = (StreamInfoBlock) mMetadata.get(0);

        // Disgusting hack: read the first frame for audio data.
        int syncCode = mSource.intFromBits(mPtr, 14, ByteOrder.BIG_ENDIAN);
        mPtr += 14;
        mFirstFrame = new Frame(mSource.bitSlice(14), mStreamInfo);
        mPtr += mFirstFrame.getLength();
    }

    public Frame nextFrame()
    {
        Frame frame;
        if (mIsFirstFrame)
        {
            mIsFirstFrame = false;
            frame =  mFirstFrame;
        }
        else
        {
            // Check to see if there are more frames left, by trying to read
            // the start-of-frame sync code.
            int syncCode = mSource.intFromBits(mPtr, 14, ByteOrder.BIG_ENDIAN);
            mPtr += 14;

            frame = new Frame(mSource.bitSlice(14), mStreamInfo);
            mPtr += frame.getLength();
        }

        sLogger.finest("Parsed frame " + frame);
        return frame;
	}
	
	public int getNumChannels()
	{
		return mStreamInfo.getNumChannels();
	}
}
