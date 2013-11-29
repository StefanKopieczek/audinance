package com.stefankopieczek.audinance.formats.flac.structure;`

import com.stefankopieczek.audinance.audiosources.EncodedSource;

public abstract class MetadataBlock
{
	public boolean mIsLastBlock;
	
	public final boolean mLength;
	
	public MetadataBlock(int length)
	{
		// Assume not last block by default.
		this(length, false);
	}
	
	public MetadataBlock(int length, boolean isLastBlock)
	{
		mLength = length;
		mIsLastBlock = isLastBlock;
	}
	
	public void setLastBlockBit(boolean isLastBlock)
	{
		mIsLastBlock = isLastBlock;
	}
	
	public static buildFromSource(EncodedSource src, int startBit)
	{
        // TODO: add selective behaviour to respect nonseekable sources
        // but otherwise not waste memory.
		boolean isLastBlock = (boolean)(src.getBit(startBit));
		
		int blockId = src.getIntFromBits(startBit + 1, 7);
		
		int length = src.getIntFromBits(startBit + 8, 24);
		
		EncodedSource blockSrc = src.bitSlice(startBit + 32, length * 8);
		
		MetadataBlock metadataBlock;
		switch (blockId)
		{
			case 0: metadataBlock = new StreamInfoBlock(length, blockSrc);
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
					break;
			default: 
					throw new UnknownBlocktypeException("Metadata block type " + blockId);
		}
		
		metadataBlock.setLastBlockBit(isLastBlock);
	}
}
