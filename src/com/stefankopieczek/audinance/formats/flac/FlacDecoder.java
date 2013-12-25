package com.stefankopieczek.audinance.formats.flac;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinance.formats.flac.structure.FlacStream;
import com.stefankopieczek.audinance.utils.BitUtils;
import com.stefankopieczek.audinancetests.audiosources.*;

/**
 * Class that handles decoding of FLAC data into raw <tt>DecodedAudio</tt>.
 */
public class FlacDecoder
{
    /**
	 * The source from which we draw the FLAC data.
	 */
	private EncodedSource mFlacSource;

	public FlacDecoder(EncodedSource flacSource)
	{
		mFlacSource = flacSource;
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
			result[idx] = mFlacSource.getByte(start + idx);
		}

		return result;
	}

	/**
	 * Decodes the FLAC data from mFlacSource and returns a DecodedAudio object with
	 * the same sample rate and number of channels.
	 * Note that decoding occurs 'just-in-time' rather than up-front, so if the FLAC
	 * data is corrupt, this may not be detected until the relevant audio is requested.
	 */
	public DecodedAudio getDecodedAudio()
	{
		FlacStream flacStream = new FlacStream(mFlacSource);
		DecodedSource[] channels = new DecodedSource[flacStream.getNumChannels()];		

		// Build a <tt>DecodedSource</tt> object for each channel that grabs and decodes
		// the FLAC data for samples as and when they are requested.
		for (int channel = 0; channel < flacStream.getNumChannels(); channel++)
		{
			final int finalChannel = channel;
			channels[channel] = new DecodedSource()
			{
				public double getSample(int idx) 
				{
					// TODO
					return 0;
				}
				
				public int getNumSamples()
				{
					// TODO
					return 0;
				}
			};
		}

		AudioFormat format = null;

		return new DecodedAudio(channels, format);		
	}

	public FlacFormat getFormat()
	{
		return null; // todo
	}
}
