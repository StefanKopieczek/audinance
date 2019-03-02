package com.kopieczek.audinance.formats.wav;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import com.kopieczek.audinance.formats.UnsupportedFormatException;
import com.kopieczek.audinance.utils.BitUtils;

import java.nio.ByteOrder;

public class WavEncoder 
{	
	static final WavEncodingType DEFAULT_ENCODING = 
			                                               WavEncodingType.PCM;
	static final short DEFAULT_BIT_DEPTH = 16;
	
	private WavFormat mTargetFormat;
	
	private DecodedAudio mRawAudio;
	
	public WavEncoder(DecodedAudio rawAudio, WavFormat targetFormat)
		throws InvalidAudioFormatException, UnsupportedWavEncodingException
	{
		if (!targetFormat.isEntirelyDetermined())
		{
			throw new InvalidAudioFormatException("Underdetermined format: " +
		                                                         targetFormat);
		}
		
		if (targetFormat.getWavEncoding() != WavEncodingType.PCM)
		{
			throw new UnsupportedWavEncodingException("Cannot encode to " + 
		                                        targetFormat.getWavEncoding());
		}
		
		mTargetFormat = targetFormat;
		
		// Resamples and [de]multiplexes the audio so that the stored raw audio
		// has the same sample rate and number of channels as the desired
		// output WAV data.
		mRawAudio = rawAudio.convertTo(targetFormat);
	}

	public WavEncoder(DecodedAudio rawAudio)
		throws InvalidAudioFormatException, UnsupportedWavEncodingException
	{
		this(rawAudio, 
		     new WavFormat(rawAudio.getFormat().getSampleRate(), 
		    		       rawAudio.getFormat().getNumChannels(), 
		    		       DEFAULT_ENCODING, 
		    		       DEFAULT_BIT_DEPTH));			
	}
	
	public EncodedSource encodeToSource()
	{
		final byte[] formatHeader = createWavHeader();
		return new EncodedSource()
		{
			@Override
			public byte getByte(int index)
				throws NoMoreDataException
			{
				if (index < formatHeader.length)
				{
					return formatHeader[index];
				}
				else
				{					
					index -= formatHeader.length;
					int bytesPerSample = mTargetFormat.getBitsPerSample() / 8;
					int sampleNum = index / bytesPerSample;					
					int channelNum = sampleNum % mTargetFormat.getNumChannels();
					
					double rawSample = mRawAudio.getChannels()[channelNum].
					     getSample(sampleNum / mTargetFormat.getNumChannels());					
					
					byte[] sampleData;
					switch (mTargetFormat.getBitsPerSample())
					{
						case 8:  sampleData = new byte[]{(byte)(rawSample/2)};
						         break;
						case 16: sampleData = BitUtils.bytesFromShort(
								              	(short)rawSample, 
								              	ByteOrder.LITTLE_ENDIAN);
								 break;
						case 32: sampleData = BitUtils.bytesFromInt(
								                (int)rawSample * 2, 
								                ByteOrder.LITTLE_ENDIAN);
								 break;
						default: throw new UnsupportedFormatException(
								 	"Unsupported bit depth: " + 
								 	mTargetFormat.getBitsPerSample());								 
					}
					
					int byteOffset = index % bytesPerSample;
					return sampleData[byteOffset];
				}
			}		
			
			public int getLength()
			{
				int result = formatHeader.length;
				DecodedSource[] channels = mRawAudio.getChannels();
				int byteDepth = mTargetFormat.getBitsPerSample() / 8;
				
				for (DecodedSource channel : channels)
				{
					result += channel.getNumSamples() * byteDepth;
				}
				
				return result;
			}
		};		
	}	
	
	public WavData encode()
	{		
		EncodedSource source = encodeToSource();
		return new WavData(source, mTargetFormat);
	}
	
	private byte[] createWavHeader()
	{
		// The WAV header is always 44 bytes long for PCM data.
		byte[] header = new byte[44];
		
		// -- Start of RIFF header -- \\
		
		// RIFF header begins with 'RIFF' in big-endian ASCII.
		header[0] = 'R';
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		
		// HACK - set size of file to UINT_MAX.
		// This needs to be fixed later as we often know what this should be.
		header[4] = makeByte(0xff);
		header[5] = makeByte(0xff);
		header[6] = makeByte(0xff);
		header[7] = makeByte(0xff);
		
		// RIFF headers for WAV files end with the format specifier 'WAVE'
		// in big-endian ascii.
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		
		// -- End of RIFF header -- \\
		
		// -- Start of fmt subchunk -- \\
		
		// fmt subchunks start with "fmt " in big-endian ascii.
		header[12] = 'f';
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		
		// The size of the fmt subchunk. This is always 16 for PCM data.
		System.arraycopy(
		    BitUtils.bytesFromInt(16, ByteOrder.LITTLE_ENDIAN), 
			0, 
			header, 
			16, 
			4);
		
		// The format of the audio data, specified as a numerical code.
		short formatCode = mTargetFormat.getWavEncoding().mCode;
		System.arraycopy(
		    BitUtils.bytesFromShort(formatCode, ByteOrder.LITTLE_ENDIAN),
			0, 
			header, 
			20, 
			2);
		
		// The number of channels the wav file wil contain.
		if (mTargetFormat.getNumChannels() > Short.MAX_VALUE)
		{
			// Wav files store the number of channels as a short, so only
			// Short.MAX_VALUE are permitted at most.
			throw new UnsupportedFormatException(
			    "Invalid value " + mTargetFormat.getNumChannels() + 
			    " for number of channels. Max " + Short.MAX_VALUE + 
			    " channels permitted.");
		}
		short numChannels = mTargetFormat.getNumChannels().shortValue();
		System.arraycopy(
		    BitUtils.bytesFromShort(numChannels, ByteOrder.LITTLE_ENDIAN),
		    0,
		    header,
		    22,
		    2);
		
		// The number of samples per second in each channel of the audio.
		int sampleRate = mTargetFormat.getSampleRate().intValue();
		System.arraycopy(
	        BitUtils.bytesFromInt(sampleRate, ByteOrder.LITTLE_ENDIAN),
			0,
			header,
			24,
			4);
		    
		// The number of bytes per second of audio across all channels.
		int byteDepth = mTargetFormat.getBitsPerSample() / 8;		
		int byteRate = sampleRate * byteDepth * numChannels;
		System.arraycopy(
		    BitUtils.bytesFromInt(byteRate, ByteOrder.LITTLE_ENDIAN),
			0,
			header,
			28,
			4);
		
		// The number of bytes in a single frame of audio across all channels.
		// TODO: What happens if this is bigger than SHORT_MAX?
		short blockAlign = (short) (byteDepth * numChannels);
		System.arraycopy(
			    BitUtils.bytesFromShort(blockAlign, ByteOrder.LITTLE_ENDIAN),
				0,
				header,
				32,
				2);
		
		// The number of bits per sample of audio in a single channel.
		short bitDepth = (short)(byteDepth * 8);
		System.arraycopy(
			    BitUtils.bytesFromShort(bitDepth, ByteOrder.LITTLE_ENDIAN),
				0,
				header,
				34,
				2);
		
		// -- End of fmt subchunk -- \\
		
		// -- Start of data subchunk -- \\
		
		// Data subchunk starts with "data" in ascii big-endian.
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		
		// Size of data payload.
		// Todo: Actually set this properly.
		header[40] = makeByte(0xff);
		header[41] = makeByte(0xff);
		header[42] = makeByte(0xff);
		header[43] = makeByte(0xff);
		
		return header;
	}
	
	/**
	 * Makes a byte by taking an integer in the range of an unsigned byte, and
	 * providing the byte which, treated as unsigned, has the equivalent value.
	 * This sidesteps the issue that Java expects bytes to be signed.
	 * @param val The value to encode as a byte.
	 * @return The byte corresponding to the value provided.
	 */
	private byte makeByte(int val)
	{
		assert ((val >= 0) && (val <= 0xff));
		
		if (val > 128)
			val = - (val - 128);
		
		return (byte)val;
	}
}
