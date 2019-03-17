package com.kopieczek.audinance.formats.wav;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import com.kopieczek.audinance.formats.wav.structure.DataSubchunk;
import com.kopieczek.audinance.formats.wav.structure.FmtSubchunk;
import com.kopieczek.audinance.formats.wav.structure.RiffChunk;

import java.util.logging.Logger;

public class WavDecoder {
    private static final Logger sLogger = Logger.getLogger(WavDecoder.class.getName());

	private final EncodedSource mWavSource;
	
	public WavDecoder(EncodedSource wavSource) {
		mWavSource = wavSource;
	}
	
	/**
	 * Decodes the WAV data from mWavSource and returns a DecodedAudio object with
	 * the same sample rate and number of channels.
	 * Note that decoding occurs 'just-in-time' rather than up-front, so if the WAV
	 * data is corrupt, this may not be detected until the relevant audio is requested.
	 *
	 * @throws InvalidWavDataException if the wav data is invalid or corrupt.
	 * @throws UnsupportedWavEncodingException if the wav data is encoded with a codec that we do not support.
	 */
	public DecodedAudio getDecodedAudio() throws InvalidWavDataException, UnsupportedWavEncodingException {
		// The entire wav file is comprised of a single RIFF chunk.
		RiffChunk riff = loadRiffChunk();

		// We assume the payload starts with a fmt subchunk, followed by the wav data chunk.
        // This isn't true in general - TODO Add support for other chunks and more complex structure.
		FmtSubchunk fmt = loadFmtSubchunk(riff);
		DataSubchunk data = loadDataSubchunk(riff, fmt);

		DecodedSource[] channels = buildChannels(fmt, data);
		AudioFormat format = new AudioFormat(fmt.getSampleRate(), (int)fmt.getNumChannels());
		
		return new DecodedAudio(channels, format);		
	}

	RiffChunk loadRiffChunk() {
		RiffChunk riffChunk = new RiffChunk(mWavSource, 0);
		sLogger.fine("Loaded riff chunk at index 0: " + riffChunk);
		return riffChunk;
	}

	FmtSubchunk loadFmtSubchunk(RiffChunk riff) {
		FmtSubchunk fmtChunk = new FmtSubchunk(mWavSource, RiffChunk.CHUNK_DATA_OFFSET_IN_BYTES, riff);
		sLogger.fine("Loaded FMT subchunk at index " + RiffChunk.CHUNK_DATA_OFFSET_IN_BYTES + ": " + fmtChunk);
		return fmtChunk;
	}

	DataSubchunk loadDataSubchunk(RiffChunk riff, FmtSubchunk fmt) {
		DataSubchunk dataChunk = new DataSubchunk(mWavSource, fmt.getEndIndex(), riff, fmt.getBitsPerSample());
		sLogger.fine("Loaded data subchunk at index " + fmt.getEndIndex() + ": " + dataChunk);
		return dataChunk;
	}

	/**
	 * Build a DecodedSource object for each channel that grabs and decodes the WAV data for samples as and when they
	 * are requested
	 */
	DecodedSource[] buildChannels(FmtSubchunk fmtChunk, DataSubchunk dataChunk) {
		final int numChannels = fmtChunk.getNumChannels();
		final int sampleDepth = fmtChunk.getBitsPerSample();

		DecodedSource[] channels = new DecodedSource[fmtChunk.getNumChannels()];
		for (int channel = 0; channel < numChannels; channel++) {
			channels[channel] = new WavChannel(dataChunk, sampleDepth, channel, numChannels);
		}

		return channels;
	}

	static class WavChannel extends DecodedSource {
	    private final DataSubchunk dataChunk;
	    private final int channelIdx;
	    private final int sampleDepth;
	    private final int totalChannels;

	    WavChannel(DataSubchunk dataChunk, int sampleDepth, int channelIdx, int totalChannels) {
	    	this.dataChunk = dataChunk;
	    	this.channelIdx = channelIdx;
	    	this.sampleDepth = sampleDepth;
	    	this.totalChannels = totalChannels;
		}

		@Override
		public double getSample(int idx) throws NoMoreDataException, InvalidAudioFormatException {
			// The index of the individual bit in the wav data that begins the frame
			// containing the desired sample.
			int frameStartIdx = sampleDepth * idx * totalChannels;

			// The number of bits at the start of the frame before the desired sample.
			int offsetToSample = channelIdx	* sampleDepth;

			// Get the sample by specifying the index as a byte.
			return dataChunk.getSample((frameStartIdx + offsetToSample) / 8);
		}

		@Override
		public int getNumSamples() {
			return dataChunk.getLength() * Byte.SIZE / (sampleDepth * totalChannels);
		}
	}
	
	public WavFormat getFormat() throws InvalidWavDataException, UnsupportedWavEncodingException {
		RiffChunk riff = loadRiffChunk();
		final FmtSubchunk fmt = loadFmtSubchunk(riff);
		return new WavFormat(fmt.getSampleRate(), (int)fmt.getNumChannels(), fmt.getEncodingType(), fmt.getBitsPerSample());
	}
}
