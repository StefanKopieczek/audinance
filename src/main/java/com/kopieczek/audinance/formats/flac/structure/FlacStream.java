package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import com.kopieczek.audinance.formats.flac.FlacFormat;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FlacStream
{
    private static final Logger sLogger = Logger.getLogger(FlacStream.class.getName());
    private static final int METADATA_START_IDX = 32;
    public static final int SYNC_CODE_LENGTH = 14;

    private int mFramesStartIdx;
	
	private List<MetadataBlock> mMetadata;

    private EncodedSource mSource;

	private StreamInfoBlock mStreamInfo;
	
	private Boolean mHasSeektable = null;
	
	private SeektableBlock mSeektableBlock;
    private final int mFirstFrameIdx;

    private Frame mCachedFrame = null;
    private int mCachedFrameIdx = 0;

    public FlacStream(EncodedSource source) {
        sLogger.fine("Constructing FlacStream for source " + source);

        // Get the metadata
        this.mSource = source;
        int ptr = METADATA_START_IDX;
        boolean moreBlocks = true;
        mMetadata = new ArrayList<MetadataBlock>();

        while (moreBlocks) {
            MetadataBlock next = MetadataBlock.buildFromSource(mSource, ptr);
            sLogger.fine("Parsed metadata block " + next + "; isLastBlock? " + next.mIsLastBlock);

            mMetadata.add(next);
            ptr += next.mLength;
            moreBlocks = !next.mIsLastBlock;
        }

        // The first metadatum is always a StreamInfoBlock.
        // Todo: handle failure better here.
        mStreamInfo = (StreamInfoBlock) mMetadata.get(0);

        mFirstFrameIdx = ptr;
    }

    public FlacFormat getFormat()
    {
        return new FlacFormat(mStreamInfo.getSampleRate(),
                              mStreamInfo.getNumChannels(),
                              mStreamInfo.getMinimumBlockSize(),
                              mStreamInfo.getMaximumBlockSize());
    }

    public DecodedAudio getDecodedAudio()
    {
        DecodedSource[] channels = new DecodedSource[getNumChannels()];
        for (int ii = 0; ii < channels.length; ii++)
        {
            final int channelIdx = ii;
            channels[channelIdx] = new DecodedSource()
            {
                @Override
                public double getSample(int sampleIdx) throws NoMoreDataException, InvalidAudioFormatException
                {
                    Frame frame = getFrameWithSample(sampleIdx);
                    return frame.getDecodedChannels()[channelIdx].getSample(sampleIdx - frame.getSampleIndex());
                }

                @Override
                public int getNumSamples()
                {
                    return mStreamInfo.getNumTotalSamples();
                }
            };
        }

        return new DecodedAudio(channels, getFormat());
    }

    private Frame getFrameWithSample(long sampleIdx)
    {
        if (mCachedFrame == null || mCachedFrame.getSampleIndex() > sampleIdx)
        {
            mCachedFrameIdx = mFirstFrameIdx;
            mCachedFrame = getFrameAt(mCachedFrameIdx);
        }

        while (mCachedFrame.getSampleIndex() + mCachedFrame.getBlockSize() < sampleIdx)
        {
            mCachedFrameIdx += SYNC_CODE_LENGTH + mCachedFrame.getLength();
            mCachedFrame = getFrameAt(mCachedFrameIdx);
        }

        return mCachedFrame;
    }

    public Frame getFrameAt(int ptr)
    {
        Frame frame;
        // Check to see if there are more frames left, by trying to read
        // the start-of-frame sync code.
        int syncCode = mSource.intFromBits(ptr, SYNC_CODE_LENGTH, ByteOrder.BIG_ENDIAN);
        ptr += SYNC_CODE_LENGTH;

        frame = new Frame(mSource.bitSlice(SYNC_CODE_LENGTH), mStreamInfo);

        sLogger.finer("Parsed frame " + frame);
        return frame;
	}
	
	public int getNumChannels()
	{
		return mStreamInfo.getNumChannels();
	}
}
