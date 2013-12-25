package com.stefankopieczek.audinance.formats.flac.structure;

import java.nio.ByteOrder;

import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.flac.InvalidFlacDataException;
import com.stefankopieczek.audinance.formats.flac.UnknownBlocktypeException;

public abstract class MetadataBlock
{
	public boolean mIsLastBlock;
	
	public final int mLength;
	
	public MetadataBlock(int length)
	{
		// Assume not last block by default.
		this(length, false);
	}
	
	public MetadataBlock(int length, boolean isLastBlock)
	{
		mLength = length + 32; // 4 bytes for Metadata header.
		mIsLastBlock = isLastBlock;
	}
	
	public void setLastBlockBit(boolean isLastBlock)
	{
		mIsLastBlock = isLastBlock;
	}
	
	public static MetadataBlock buildFromSource(EncodedSource src, int startBit)
	{
        // TODO: add selective behaviour to respect nonseekable sources
        // but otherwise not waste memory.	
		boolean isLastBlock = src.getBit(startBit) == 1;		
		int blockId = src.intFromBits(startBit + 1, 7, ByteOrder.BIG_ENDIAN);		
		int length = src.intFromBits(startBit + 8, 24, ByteOrder.BIG_ENDIAN);		
		EncodedSource blockSrc = src.bitSlice(startBit + 32, length * 8);		
		
		MetadataBlock metadataBlock;
		switch (blockId)
		{
			case 0: metadataBlock = new StreamInfoBlock(blockSrc);
			        break;
			case 1: metadataBlock = new PaddingBlock(length);
			        break;
			case 2: metadataBlock = new ApplicationBlock(length, blockSrc);
			        break;
			case 3: metadataBlock = new SeektableBlock(length, blockSrc);
			        break;
			case 4: metadataBlock = new VorbisCommentBlock(length, blockSrc);
			        break;
			case 5: metadataBlock = new CuesheetBlock(length, blockSrc);
			        break;
			case 6: metadataBlock = new PictureBlock(length, blockSrc);
			        break;
			case 127: 
			        throw new InvalidFlacDataException(
						"Metadata block has invalid block type code 127.");					
			default: 
					throw new UnknownBlocktypeException("Metadata block type " + blockId);
		}
		
		metadataBlock.setLastBlockBit(isLastBlock);
		
		return metadataBlock;
	}
}
