package com.stefankopieczek.audinance.formats.flac.structure;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

public class FlacStream
{
	private static final int METADATA_START_IDX = 32;
	
	private int mFramesStartIdx;
	
	private ArrayList<MetadataBlock> mMetadata;
	
	private ArrayList<Frame> mFrames;
	
	private StreamInfoBlock mStreamInfo;
	
	private Boolean mHasSeektable = null;
	
	private SeekTable mSeektableBlock;
	
	public FlacStream(EncodedSource source)
	{
		// Get the metadata
		int ptr = METADATA_START_IDX;
		boolean moreBlocks = true;
		while (moreBlocks)
		{
			MetadataBlock next = MetadataBlock.buildFromSource(source, ptr);
			mMetadata.add(next);
			ptr += next.mLength;
			moreBlocks = next.mIsLastBlock;
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
                int syncCode = source.intFromBits(ptr, 14);
                ptr += 14;
            }
            catch (NoMoreDataException e)
            {
                break;
            }
            
            Frame next = new Frame(source.bitSlice(14));
            mFrames.add(next); // this is silly and wasteful
            ptr += next.mLength;
        }
	}
}
