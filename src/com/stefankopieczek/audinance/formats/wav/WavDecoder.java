package com.stefankopieczek.audinance.formats.wav;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.logging.Logger;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.audiosources.NoMoreDataException;
import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinance.utils.BitUtils;

/**
 * Class that handles decoding of WAV data into raw <tt>DecodedAudio</tt>.
 */
public class WavDecoder
{
    private static final Logger sLogger = Logger.getLogger(WavDecoder.class.getName());

    /**
	 * The source from which we draw the WAV data.
	 */
	private EncodedSource mWavSource;
	
	public WavDecoder(EncodedSource wavSource)
	{
		mWavSource = wavSource;
	}
	
	/**
	 * Gets an array of bytes of the given length from the data source, starting
	 * at the given index.
	 * TODO: Why isn't this a method of EncodedSource?
	 *
	 * @param start The byte index to start reading from in the encoded source.
	 * @param length The number of bytes to read.
	 * @return 'length' bytes from mWavSource, starting at index 'start'.
	 */
	protected byte[] getRange(int start, int length)
	{				
		byte[] result = new byte[length];
		
		for (int idx = 0; idx < length; idx++)
		{
			result[idx] = mWavSource.getByte(start + idx);
		}
		
		return result;
	}
	
	/**
	 * Decodes the WAV data from mWavSource and returns a DecodedAudio object with
	 * the same sample rate and number of channels.
	 * Note that decoding occurs 'just-in-time' rather than up-front, so if the WAV
	 * data is corrupt, this may not be detected until the relevant audio is requested.
	 *
	 * @throws InvalidWavDataException if the wav data is invalid or corrupt.
	 * @throws UnsupportedWavEncodingException if the wav data is encoded with a codec that
	 *         we do not support.
	 */
	public DecodedAudio getDecodedAudio()
		throws InvalidWavDataException, UnsupportedWavEncodingException
	{
		// The entire wav file is comprised of a single RIFF chunk.
		RiffChunk riffChunk = new RiffChunk(0);
        sLogger.fine("Loaded riff chunk at index 0: " + riffChunk);

		// The RIFF chunk header specifies where the RIFF payload starts.
		// We assume the payload starts with a fmt subchunk.
		// TODO: Add support for other chunks and more complex structure.
		final FmtSubchunk fmtChunk = new FmtSubchunk(RiffChunk.DATA_IDX_OFFSET, 
				                                     riffChunk);
        sLogger.fine("Loaded FMT subchunk at index " + RiffChunk.DATA_IDX_OFFSET + ": " + fmtChunk);


		// We assume the next subchunk in the RIFF payload is the wav data chunk.
		final DataSubchunk dataChunk = new DataSubchunk(fmtChunk.getEndIndex(), 
				                                        riffChunk,
				                                        fmtChunk.getBitsPerSample());
		sLogger.fine("Loaded data subchunk at index " + fmtChunk.getEndIndex() + ": " + dataChunk);
		
		DecodedSource[] channels = new DecodedSource[fmtChunk.getNumChannels()];		
		
		// Build a <tt>DecodedSource</tt> object for each channel that grabs and decodes
		// the WAV data for samples as and when they are requested.
		for (int channel = 0; channel < fmtChunk.getNumChannels(); channel++)
		{
			final int finalChannel = channel;
			channels[channel] = new DecodedSource()
			{
				public double getSample(int idx)
					throws InvalidWavDataException, NoMoreDataException
				{
					// The index of the individual bit in the wav data that begins the frame
					// containing the desired sample.
					int frameStartIdx = fmtChunk.getBitsPerSample() *
							            idx * 
							            fmtChunk.getNumChannels();
										
					// The number of bits at the start of the frame before the desired sample.
					int offsetToSample = finalChannel * fmtChunk.getBitsPerSample();
													  
					// Get the sample by specifying the index as a byte.
					return dataChunk.getSample((frameStartIdx + offsetToSample) / 8);
				}
				
				public int getNumSamples()
				{
					return (int)(dataChunk.getLength() * 8.0 / (dataChunk.mBitsPerSample * fmtChunk.getNumChannels()));
				}
			};
		}
		
		AudioFormat format = new AudioFormat(fmtChunk.getSampleRate(),
				                             (int)fmtChunk.getNumChannels());
		
		return new DecodedAudio(channels, format);		
	}
	
	public WavFormat getFormat()
		throws InvalidWavDataException, UnsupportedWavEncodingException
	{
		RiffChunk riffChunk = new RiffChunk(0);
		final FmtSubchunk fmtChunk = new FmtSubchunk(RiffChunk.DATA_IDX_OFFSET, 
				                                     riffChunk);
		return new WavFormat(fmtChunk.getSampleRate(),
				             (int)fmtChunk.getNumChannels(),
				             fmtChunk.getEncodingType(),
				             fmtChunk.getBitsPerSample());
	}
	
	private abstract class Chunk
	{
		private Integer mStartIdx;
		private Integer mEndIdx;
		private Integer mLength;
		
		protected abstract int getChunkSizeIdxOffset();
		
		public Chunk(int startIdx) throws InvalidWavDataException
		{
			mStartIdx = startIdx;
		}
		
		public abstract ByteOrder getEndianism() throws InvalidWavDataException; 
		
		protected int getStartIndex()
		{
			return mStartIdx.intValue();
		}
		
		protected int getEndIndex() throws InvalidWavDataException
		{
			if (mEndIdx == null)
				mEndIdx = mStartIdx + getLength();
				
			return mEndIdx.intValue();
		}
		
		protected int getLength() throws InvalidWavDataException
		{
			int chunkSizeIdx = mStartIdx + getChunkSizeIdxOffset();
			
			if (mLength == null)
			{
				int lengthValue = BitUtils.intFromBytes(
						                   getRange(chunkSizeIdx, 4), getEndianism());
				 mLength = getChunkSizeIdxOffset() + 4 + lengthValue;			                     
			}
			
			return mLength.intValue();
		}
		
		protected short getShort(int idx) throws InvalidWavDataException
		{
			byte[] bytes = getRange(getStartIndex() + idx, 2);
			return BitUtils.shortFromBytes(bytes, getEndianism());
		}
		
		protected int getInt(int idx) throws InvalidWavDataException
		{
			byte[] bytes = getRange(getStartIndex() + idx, 4);
			return BitUtils.intFromBytes(bytes, getEndianism());
		}
	}
	
	private class RiffChunk extends Chunk
	{
		private static final int ID_IDX_OFFSET = 0;
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;
		private static final int DATA_IDX_OFFSET = 12;
		
		private ByteOrder mEndianism;
		
		public RiffChunk(int startIdx) throws InvalidWavDataException
		{
			super(startIdx);
		}		
		
		public ByteOrder getEndianism() throws InvalidWavDataException
		{
			if (mEndianism == null)
			{
				String chunkId = null;
				try
				{
					chunkId = BitUtils.stringFromBytes(getRange(ID_IDX_OFFSET, 4));
				}
				catch (UnsupportedEncodingException e)
				{
					throw new InvalidWavDataException("Invalid RIFF header ID format.", 
						                          	  e);
				}
				
				if (chunkId.equals("RIFF"))
				{
					mEndianism = ByteOrder.LITTLE_ENDIAN;
				}
				else if (chunkId.equals("RIFX"))
				{
					mEndianism = ByteOrder.BIG_ENDIAN;
				}
				else
				{
					throw new InvalidWavDataException("Invalid RIFF header ID " + 
			                                          chunkId);				
				}
			}
			
			return mEndianism;
		}
		
		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}		
	}
	
	private class FmtSubchunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;
		private static final int FORMAT_CODE_IDX_OFFSET = 8;
		private static final int NUM_CHANNELS_IDX_OFFSET = 10;
		private static final int SAMPLE_RATE_IDX_OFFSET = 12;
		private static final int BYTE_RATE_IDX_OFFSET = 16;
		private static final int BLOCK_ALIGN_IDX_OFFSET = 20;
		private static final int BITS_PER_SAMPLE_IDX_OFFSET = 22;
		private static final int EXTRA_PARAMS_SIZE_IDX_OFFSET = 24;
		private static final int EXTRA_PARAMS_IDX_OFFSET = 26;
		
		private RiffChunk mParent;
		private Short mFormatCode;
		private Short mNumChannels;
		private Integer mSampleRate;
		private Integer mByteRate;
		private Short mBlockAlign;
		private Short mBitsPerSample;
		private Integer mExtraParamsSize;

		public FmtSubchunk(int startIdx, RiffChunk parent)
			throws InvalidWavDataException
		{
			super(startIdx);
			mParent = parent;
		}
		
		public ByteOrder getEndianism() throws InvalidWavDataException
		{
			return mParent.getEndianism();
		}

		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}				
		
		public short getFormatCode() throws InvalidWavDataException
		{
			if (mFormatCode == null)
			{
				mFormatCode = getShort(FORMAT_CODE_IDX_OFFSET);
			}
			
			return mFormatCode.shortValue();
		}
		
		public WavEncodingType getEncodingType()
			throws UnsupportedWavEncodingException, InvalidWavDataException
		{
			return WavEncodingType.getEncodingTypeFromCode(getFormatCode());
		}
		
		public short getNumChannels() throws InvalidWavDataException
		{
			if (mNumChannels == null)
			{
				mNumChannels = getShort(NUM_CHANNELS_IDX_OFFSET);
			}
			
			return mNumChannels.shortValue();
		}
				
		public int getSampleRate() throws InvalidWavDataException
		{
			if (mSampleRate == null)
			{
				mSampleRate = getInt(SAMPLE_RATE_IDX_OFFSET);
			}
			
			return mSampleRate.intValue();
		}
		
		public int getByteRate() throws InvalidWavDataException
		{
			if (mByteRate == null)
			{
				mByteRate = getInt(BYTE_RATE_IDX_OFFSET);
			}
			
			return mByteRate.intValue();
		}
		
		public short getBlockAlign() throws InvalidWavDataException
		{
			if (mBlockAlign == null)
			{
				mBlockAlign = getShort(BLOCK_ALIGN_IDX_OFFSET);
			}
			
			return mBlockAlign.shortValue();
		}
		
		public short getBitsPerSample() throws InvalidWavDataException
		{
			if (mBitsPerSample == null)
			{
				mBitsPerSample = getShort(BITS_PER_SAMPLE_IDX_OFFSET);
			}
			
			return mBitsPerSample.shortValue();
		}
	}
	
	private class DataSubchunk extends Chunk
	{
		private static final int CHUNK_SIZE_IDX_OFFSET = 4;
		private static final int DATA_IDX_OFFSET = 8;		
		
		private RiffChunk mParent;
		private int mBitsPerSample;

		public DataSubchunk(int startIdx, 
				            RiffChunk parent,
				            int bitsPerSample)
			throws InvalidWavDataException
		{
			super(startIdx);
			mParent = parent;
			mBitsPerSample = bitsPerSample;
		}

		public ByteOrder getEndianism() throws InvalidWavDataException
		{
			return mParent.getEndianism();
		}
		
		protected int getChunkSizeIdxOffset()
		{
			return CHUNK_SIZE_IDX_OFFSET;
		}
		
		public double getSample(int byteIdx)
				throws InvalidWavDataException, NoMoreDataException
		{
			double result;						
			long endOfSample = (long)(byteIdx + Math.ceil(mBitsPerSample / 8.0));
			if (endOfSample >= getLength())
			{
				throw new NoMoreDataException();
			}
			
			if (mBitsPerSample == 8)
			{			
				int tempResult = mWavSource.getByte(getStartIndex() + byteIdx);				
				tempResult &= 0xFF; // Don't treat the byte as signed.
				result = tempResult * 2; // Normalise energy of sample to match 16bitPCM.
				
			}
			else if (mBitsPerSample == 16)
			{
				byte[] bytes = getRange(getStartIndex() + byteIdx, 2);
				result = BitUtils.shortFromBytes(bytes, getEndianism());
			}
			else if (mBitsPerSample == 32)
			{
				byte[] bytes = getRange(getStartIndex() + byteIdx, 4);
				// Currently assuming 32-bit int; float to come later.
				// Divide by 65536 = 2^16 to normalise to same energy as 16 bit.
				result = BitUtils.intFromBytes(bytes, getEndianism()) / 65536.0;				
			}
			else
			{
				throw new InvalidWavDataException("Unsupported bit depth: " + 
			                                                      mBitsPerSample);
			}
			
			return result;
		}
	}
}
