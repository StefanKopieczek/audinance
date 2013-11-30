package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.stefankopieczek.audinance.audiosources.EncodedSource;

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
		mSrc = source;
		mStreamInfo = streamInfo;
		
		int ptr = getFirstSubframeIdx();
		
		for (int subframeNum = 0; subframeNum < getNumChannels(); subframeNum++)
		{
			// TODO: enforce caching for streams
			mSubframes.add(Subframe.buildFromSource(mSrc.bitSlice(ptr), this));
		}
	}
	
	public boolean isVariableBlocksize()
	{
		if (mIsVariableBlocksize == null)
		{
			int blockingStrategyBit = mSrc.getBit(2);
			mIsVariableBlocksize = (blockingStrategyBit == 1);
		}
		
		return mIsVariableBlocksize.booleanValue();
	}
	
	public int getLength()
	{
		return 0; // TODO
	}
	
	public int getBlockSize()
	{
		if (mBlockSize == null)
		{
			int sizeCode = mSrc.intFromBits(2, 4, ByteOrder.BIG_ENDIAN);
			
			switch (sizeCode)
			{
				case 1:  mBlockSize = 192; break;
				case 2:  mBlockSize = 576; break;
				case 3:  mBlockSize = 1152; break;
				case 4:  mBlockSize = 2304; break;
				case 5:  mBlockSize = 4608; break;
				case 6:
					// TODO
				case 7:
					//TODO
				case 8:  mBlockSize = 256; break;
				case 9:  mBlockSize = 512; break;
				case 10: mBlockSize = 1024; break;
				case 11: mBlockSize = 2048; break;
				case 12: mBlockSize = 4096; break;
				case 13: mBlockSize = 8192; break;
				case 14: mBlockSize = 16384; break;
				case 15: mBlockSize = 32768; break;
				default: //error TODO
			}
		}
		
		return mBlockSize.intValue();
	}
	
	private int getFirstSubframeIdx()
	{
		return 0; // TODO
	}
	
	public int getNumChannels()
	{
		if (mNumChannels == null)
		{
			//TODO
		}
		
		return mNumChannels.intValue();
	}
	
	public enum ChannelStrategy
	{
		SEPARATE,
		LEFT_SIDE_STEREO,
		RIGHT_SIDE_STEREO,
		MID_SIDE_STEREO;
	}
}
