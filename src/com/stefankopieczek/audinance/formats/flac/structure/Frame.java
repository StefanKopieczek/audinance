package com.stefankopieczek.audinance.formats.flac.structure;

public class Frame
{
	private EncodedSource mSrc;
	
	private StreamInfoBlock mStreamInfo;
	
	private Boolean mIsVariableBlocksize;
	
	private Integer mBlockSize;
	
	private Integer mSampleRate;
	
	private Integer mNumChannels;
	
	private ChannelStrategy mChannelStrategy;
	
	private Integer mSampleSize;
	
	private Integer mSampleIdx;
	
	private Integer mFrameIdx;
	
	private Integer mHeaderChecksum;
	
	private Integer mFooterChecksum;
	
	private List<Subframe> mSubframes = new ArrayList<Subframe>();
	
	/**
	 * Build a Flac frame out of encoded flac data.
	 * Draws data from the given source, which it expects to 
	 * point to the data immediately following a flac sync code.
	 */
	public Frame(EncodedSource source, 
				 StreamInfoBlock streamInfo)
	{
		mSrc = src;
		mStreamInfo = streamInfo;
		
		int ptr = getFirstSubframeIdx();
		
		for (int subframeNum = 0; subframeNum < getNumChannels(); subframeNum++)
		{
			// todo: enforce caching for streams
			mSubframes.add(Subframe.buildFromSource(mSrc.bitSlice(ptr));
		}
	}
	
	private boolean isVariableBlocksize()
	{
		if (mIsVariableBlocksize == null)
		{
			boolean blockingStrategyBit = mSrc.getBit(2);
			mIsVariableBlocksize = blockingStrategyBit;
		}
		
		return mIsVariableBlocksize.booleanValue();
	}
	
	private int getBlockSize()
	{
		if (mBlockSize == null)
		{
			int sizeCode = src.intFromBits(2, 4);
			
			switch (sizeCode)
			{
				case 1:  mBlockSize = 192; break;
				case 2:  mBlockSize = 576; break;
				case 3:  mBlockSize = 1152; break;
				case 4:  mBlockSize = 2304; break;
				case 5:  mBlockSize = 4608; break;
				case 6:
					// todo
				case 7:
					//todo
				case 8:  mBlockSize = 256; break;
				case 9:  mBlockSize = 512; break;
				case 10: mBlockSize = 1024; break;
				case 11: mBlockSize = 2048; break;
				case 12: mBlockSize = 4096; break;
				case 13: mBlockSize = 8192; break;
				case 14: mBlockSize = 16384; break;
				case 15: mBlockSize = 32768; break;
				default: //error todo
			}
		}
		
		return mBlockSize.intValue();
	}
	
	private int getFirstSubframeIdx()
	{
		return 0; // todo
	}
	
	public enum ChannelStrategy
	{
		SEPARATE,
		LEFT_SIDE_STEREO,
		RIGHT_SIDE_STEREO,
		MID_SIDE_STEREO;
	}
}
