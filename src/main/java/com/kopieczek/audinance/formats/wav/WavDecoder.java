package com.kopieczek.audinance.formats.wav;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.wav.structure.DataSubchunk;
import com.kopieczek.audinance.formats.wav.structure.FmtSubchunk;
import com.kopieczek.audinance.formats.wav.structure.RiffChunk;

import java.util.logging.Logger;

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
		RiffChunk riffChunk = new RiffChunk(mWavSource, 0);
        sLogger.fine("Loaded riff chunk at index 0: " + riffChunk);

		// The RIFF chunk header specifies where the RIFF payload starts.
		// We assume the payload starts with a fmt subchunk.
		// TODO: Add support for other chunks and more complex structure.
		final FmtSubchunk fmtChunk = new FmtSubchunk(mWavSource, RiffChunk.CHUNK_DATA_OFFSET_IN_BYTES, riffChunk);
        sLogger.fine("Loaded FMT subchunk at index " + RiffChunk.CHUNK_DATA_OFFSET_IN_BYTES + ": " + fmtChunk);


		// We assume the next subchunk in the RIFF payload is the wav data chunk.
		final DataSubchunk dataChunk = new DataSubchunk(mWavSource, fmtChunk.getEndIndex(), riffChunk, fmtChunk.getBitsPerSample());
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
					return (int)(dataChunk.getLength() * 8.0 / (fmtChunk.getBitsPerSample() * fmtChunk.getNumChannels()));
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
		RiffChunk riffChunk = new RiffChunk(mWavSource, 0);
		final FmtSubchunk fmtChunk = new FmtSubchunk(mWavSource, RiffChunk.CHUNK_DATA_OFFSET_IN_BYTES, riffChunk);
		return new WavFormat(fmtChunk.getSampleRate(),
				             (int)fmtChunk.getNumChannels(),
				             fmtChunk.getEncodingType(),
				             fmtChunk.getBitsPerSample());
	}

}
