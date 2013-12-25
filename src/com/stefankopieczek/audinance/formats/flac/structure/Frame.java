package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.flac.InvalidFlacDataException;

public class Frame
{
	private EncodedSource mSrc;
	
	private StreamInfoBlock mStreamInfo;
	
	private Boolean mIsVariableBlocksize;
	
	private Integer mBlockSize;
	
	private Integer mBlockSizeExtraBits;
	
	private Integer mSampleRate;
	
	private Integer mSampleRateExtraBits;
	
	private Integer mNumChannels;
	
	private ChannelStrategy mChannelStrategy;
	
	private Integer mBitsPerSample;
	
	private Integer mSampleIdx;
	
	private Integer mFrameIdx;
	
	private Integer mIndexFieldLength;
	
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
			Subframe next = Subframe.buildFromSource(mSrc.bitSlice(ptr), this); 
			mSubframes.add(next);
			ptr += next.getSize();
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
		throw new RuntimeException("TODO Frame length not implemented yet."); // TODO
	}
	
	public int getBlockSize()
	{
		if (mBlockSize == null)
		{
			int sizeCode = mSrc.intFromBits(2, 4, ByteOrder.BIG_ENDIAN);
			mBlockSizeExtraBits = 0;
			
			switch (sizeCode)
			{
				case 1:  mBlockSize = 192; break;
				case 2:  mBlockSize = 576; break;
				case 3:  mBlockSize = 1152; break;
				case 4:  mBlockSize = 2304; break;
				case 5:  mBlockSize = 4608; break;
				case 6:
					mBlockSizeExtraBits = 8;					
					mBlockSize = getBlockSizeFromEnd(mBlockSizeExtraBits);					
					break;
				case 7:
					mBlockSizeExtraBits = 16;
					mBlockSize = getBlockSizeFromEnd(mBlockSizeExtraBits);
					break;
				case 8:  mBlockSize = 256; break;
				case 9:  mBlockSize = 512; break;
				case 10: mBlockSize = 1024; break;
				case 11: mBlockSize = 2048; break;
				case 12: mBlockSize = 4096; break;
				case 13: mBlockSize = 8192; break;
				case 14: mBlockSize = 16384; break;
				case 15: mBlockSize = 32768; break;
				default: 
					throw new InvalidFlacDataException("Unknown blocksize indication " + sizeCode);
			}
		}
		
		return mBlockSize.intValue();
	}
	
	private int getBlockSizeFromEnd(int bitsUsed)
	{		
		int startIdx = 18 + getIndexFieldLength();
		
		return mSrc.intFromBits(startIdx, bitsUsed, ByteOrder.BIG_ENDIAN);		
	}
	
	private int getBlockSizeExtraBits()
	{
		if (mBlockSizeExtraBits == null)
			getBlockSize();
		
		return mBlockSizeExtraBits;
	}
	
	public int getSampleRate()
	{
		if (mSampleRate == null)
		{
			int rateCode = mSrc.intFromBits(5, 4, ByteOrder.BIG_ENDIAN);
			mSampleRateExtraBits = 0;
			
			switch (rateCode)
			{
				case 0:  mSampleRate = mStreamInfo.getSampleRate(); break;
				case 1:  mSampleRate = 882000; break;
				case 2:  mSampleRate = 176400; break;
				case 3:  mSampleRate = 192000; break;
				case 4:  mSampleRate = 8000; break;
				case 5:  mSampleRate = 16000; break;
				case 6:  mSampleRate = 220500; break;
				case 7:  mSampleRate = 24000; break;
				case 8:  mSampleRate = 32000; break;
				case 9:  mSampleRate = 44100; break;
				case 10: mSampleRate = 48000; break;
				case 11: mSampleRate = 96000; break;
				case 12:
					mSampleRateExtraBits = 8;
					mSampleRate = getSampleRateFromEnd(mSampleRateExtraBits, 1000);
					break;
				case 13:
					mSampleRateExtraBits = 16;
					mSampleRate = getSampleRateFromEnd(mSampleRateExtraBits, 1);
					break;					
				case 14:
					mSampleRateExtraBits = 16;
					mSampleRate = getSampleRateFromEnd(mSampleRateExtraBits, 10);
					break;
				case 15:
					// error TODO
					break;
			}					
		}	
		
		return mSampleRate.intValue();
	}
	
	private int getSampleRateFromEnd(int bitsUsed, int scale)
	{
		int startIdx = 18 + getIndexFieldLength() + getBlockSizeExtraBits();
		
		return mSrc.intFromBits(startIdx, bitsUsed, ByteOrder.BIG_ENDIAN) * scale;
	}
	
	private int getSampleRateExtraBits()
	{
		if (mSampleRateExtraBits == null)
			getSampleRate();
		
		return mSampleRateExtraBits;
	}
	
	private int getNumChannels()	
	{
		if (mNumChannels == null)
		{
			// Slightly unpleasant; we get the number of channels as a side
			// effect of determining the channel strategy. 
			// This prevents code repetition, although side effects are 
			// obviously evil.			
			getChannelStrategy();
		}
		
		return mNumChannels.intValue();
	}
	
	private ChannelStrategy getChannelStrategy()
	{
		if (mChannelStrategy == null)
		{
			int channelCode = mSrc.intFromBits(9, 4, ByteOrder.BIG_ENDIAN);
			if (channelCode < 8)
			{
				mChannelStrategy = ChannelStrategy.SEPARATE;
				mNumChannels = channelCode - 1;
			}
			else if (channelCode == 9)
			{
				mChannelStrategy = ChannelStrategy.LEFT_SIDE_STEREO;
				mNumChannels = 2;
			}
			else if (channelCode == 10)
			{
				mChannelStrategy = ChannelStrategy.RIGHT_SIDE_STEREO;
				mNumChannels = 2;
			}
			else if (channelCode == 11)
			{
				mChannelStrategy = ChannelStrategy.MID_SIDE_STEREO;
				mNumChannels = 2;
			}
			else
			{
				// error TODO
			}
		}
		
		return mChannelStrategy;
	}
	
	public int getBitsPerSample()
	{
		if (mBitsPerSample == null)
		{
			int sizeBits = mSrc.intFromBits(13, 3, ByteOrder.BIG_ENDIAN);
			switch (sizeBits)
			{
				case 0: mBitsPerSample = mStreamInfo.getBitsPerSample(); break;
				case 1: mBitsPerSample = 8; break;
				case 2: mBitsPerSample = 12; break;
				case 3: // error TODO
				case 4: mBitsPerSample = 16; break;
				case 5: mBitsPerSample = 20; break;
				case 6: mBitsPerSample = 24; break;
				default: // error TODO
			}			
		}
				
		return mBitsPerSample.intValue();	
	}
	
	private int getSampleIdx()
	{
		if (mSampleIdx == null)
		{
			calculateIndexValues();		
		}
		
		return mSampleIdx;
	}
	
	private int getFrameIdx()
	{
		if (mFrameIdx == null)
		{
			calculateIndexValues();			
		}
		
		return mFrameIdx;
	}
	
	private void calculateIndexValues()
	{
		// TODO: Get frame and sample index, and length of bitfield.
	}
	
	private int getIndexFieldLength()
	{
		if (mIndexFieldLength == null)
		{
			calculateIndexValues();
		}
		
		return mIndexFieldLength;
	}
	
	private int getFirstSubframeIdx()
	{
		if (mBlockSizeExtraBits == null)
		{
			// Force calculation of the block size in order to see if extra
			// bits at the end of the header have been used to store it.
			// We don't need the result here; just for mBlockSizeExtraBits to
			// be set; so we throw the answer away.
			getBlockSize();
		}
		
		if (mSampleRateExtraBits == null)
		{
			// As above, but for the sample rate.
			getSampleRate();
		}
		
		// The header is 21+x bits long, where 'x' is the number of extra bits
		// used to store the block size and sample rate.
		// (The first few fields always use 17 bits, then the optional extra 
		// bits follow, and then an 8-bit checksum.
		return 26 + getIndexFieldLength() + getBlockSizeExtraBits() + 
				                                      getSampleRateExtraBits();
	}	
	
	public int getHeaderChecksum()
	{
		int startIdx = getFirstSubframeIdx() - 8;
		
		return mSrc.intFromBits(startIdx, 8, ByteOrder.BIG_ENDIAN);
	}
	
	public enum ChannelStrategy
	{
		SEPARATE,
		LEFT_SIDE_STEREO,
		RIGHT_SIDE_STEREO,
		MID_SIDE_STEREO;
	}
}
