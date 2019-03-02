package com.stefankopieczek.audinance.formats.flac;

import com.stefankopieczek.audinance.audiosources.DecodedSource;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.formats.AudioFormat;
import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinance.formats.flac.structure.FlacStream;

import java.util.logging.Logger;

/**
 * Class that handles decoding of FLAC data into raw <tt>DecodedAudio</tt>.
 */
public class FlacDecoder
{
    private static final Logger sLogger = Logger.getLogger(FlacDecoder.class.getName());

    /**
	 * The source from which we draw the FLAC data.
	 */
	private EncodedSource mFlacSource;
	private FlacStream mFlacStream;

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

	private FlacStream getFlacStream()
	{
		if (mFlacStream == null)
			mFlacStream = new FlacStream(mFlacSource);

		return mFlacStream;
	}

	/**
	 * Decodes the FLAC data from mFlacSource and returns a DecodedAudio object with
	 * the same sample rate and number of channels.
	 * Note that decoding occurs 'just-in-time' rather than up-front, so if the FLAC
	 * data is corrupt, this may not be detected until the relevant audio is requested.
	 */
	public DecodedAudio getDecodedAudio()
	{
		return getFlacStream().getDecodedAudio();
	}

	public FlacFormat getFormat()
	{
		return getFlacStream().getFormat();
	}
}
