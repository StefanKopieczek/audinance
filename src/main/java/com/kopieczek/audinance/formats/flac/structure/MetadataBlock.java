package com.kopieczek.audinance.formats.flac.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.flac.InvalidFlacDataException;
import com.kopieczek.audinance.formats.flac.UnknownBlocktypeException;

import java.nio.ByteOrder;

public abstract class MetadataBlock
{
	public boolean mIsLastBlock;
	
	public final int mLength;
	
	public MetadataBlock(int length)
	{
		// Assume not last block by default.
		this(length, false);
	}
	
	public MetadataBlock(int lengthBits, boolean isLastBlock)
	{
		mLength = lengthBits + 32; // 4 bytes for Metadata header.
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
		int lengthBits = src.intFromBits(startBit + 8, 24, ByteOrder.BIG_ENDIAN) * 8;
		EncodedSource blockSrc = src.bitSlice(startBit + 32, lengthBits * 8);
		
		MetadataBlock metadataBlock;
		switch (blockId)
		{
			case 0: metadataBlock = new StreamInfoBlock(blockSrc);
			        break;
			case 1: metadataBlock = new PaddingBlock(lengthBits);
			        break;
			case 2: metadataBlock = new ApplicationBlock(lengthBits, blockSrc);
			        break;
			case 3: metadataBlock = new SeektableBlock(lengthBits, blockSrc);
			        break;
			case 4: metadataBlock = new VorbisCommentBlock(lengthBits, blockSrc);
			        break;
			case 5: metadataBlock = new CuesheetBlock(lengthBits, blockSrc);
			        break;
			case 6: metadataBlock = new PictureBlock(lengthBits, blockSrc);
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
