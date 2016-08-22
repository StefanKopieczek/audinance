package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.audiosources.NoMoreDataException;

import java.nio.ByteOrder;
import java.util.ArrayList;

public class FlacStream
{
	private static final int METADATA_START_IDX = 32;
	
	private int mFramesStartIdx;
	
	private ArrayList<MetadataBlock> mMetadata;
	
	private ArrayList<Frame> mFrames;
	
	private StreamInfoBlock mStreamInfo;
	
	private Boolean mHasSeektable = null;
	
	private SeektableBlock mSeektableBlock;
	
	public FlacStream(EncodedSource source)
	{
		// Get the metadata
		int ptr = METADATA_START_IDX;
		boolean moreBlocks = true;
		mMetadata = new ArrayList<MetadataBlock>();
		
		while (moreBlocks)
		{
			MetadataBlock next = MetadataBlock.buildFromSource(source, ptr);
			mMetadata.add(next);
			ptr += next.mLength;
			moreBlocks = !next.mIsLastBlock;
		}
		
		// The first metadatum is always a StreamInfoBlock.
		// Todo: handle failure better here.
		mStreamInfo = (StreamInfoBlock)mMetadata.get(0);
		
		// Get the audio data from the frames.
        while (true)
        {
            // Check to see if there are more frames left, by trying to read
            // the start-of-frame sync code.
            try
            {
                int syncCode = source.intFromBits(ptr, 14, ByteOrder.BIG_ENDIAN);
                ptr += 14;
            }
            catch (NoMoreDataException e)
            {
                break;
            }
            
            Frame next = new Frame(source.bitSlice(14), mStreamInfo);
            mFrames.add(next); // this is silly and wasteful
            ptr += next.getLength();
        }
	}
	
	public int getNumChannels()
	{
		return mStreamInfo.getNumChannels();
	}
}
