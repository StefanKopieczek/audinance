package com.kopieczek.audinance.conversion.resamplers;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;

import java.util.logging.Logger;

/**
 * A resampler that approximates points between known samples by taking an (unweighted) average of its immediate neighbours.
 */
public class NaiveResampler implements Resampler {
    private static final Logger log = Logger.getLogger(NaiveResampler.class.getName());

	public DecodedAudio resample(DecodedAudio original, Integer targetSampleRate) {
		DecodedSource[] originalChannels = original.getChannels();
		AudioFormat originalFormat = original.getFormat();
		Integer originalSampleRate = originalFormat.getSampleRate();
        log.info("Resampling " + original + " from " + originalSampleRate + "Hz to " + targetSampleRate + "Hz");

		AudioFormat newFormat = new AudioFormat(targetSampleRate, originalFormat.getNumChannels());
		
		DecodedSource[] newChannels = new DecodedSource[originalChannels.length];
	
		// Build a new DecodedAudio object composed of resampled sources at the new frequency.
		for (int ii = 0; ii < newChannels.length; ii++) {
			newChannels[ii] = new ResamplingSource(originalChannels[ii], originalSampleRate, targetSampleRate);
		}
		
		return new DecodedAudio(newChannels, newFormat);
	}
	
	/**
	 * Resampling source for a single channel of audio.
     * Wraps the original DecodedSource channel, and performs resampling on the fly.
	 * Approximates samples that do not line up with the original data by* taking an unweighted average of the two
	 * immediate neighbour points.
	 */
	private static class ResamplingSource extends DecodedSource {
		private final DecodedSource originalChannel;
		
		/**
         * The ratio of original sample rate to desired sample rate.
		 * For example, if the original rate is 20kHz, and the desired rate is 40Khz, the scale factor is 0.5.
		 */
		private final float scaleFactor;
		
		/**
		 * The tolerance we use for assessing equality of doubles.
		 * This is used to judge if two instants in time are equal, so that we can re-use points in the original data
		 * that are close to the desired sample points.
		 */
		private static final float IDX_TOLERANCE = 0.0001f;
		
		public ResamplingSource(DecodedSource originalChannel, int originalSampleRate, int targetSampleRate) {
			this.originalChannel = originalChannel;
			scaleFactor = originalSampleRate * 1.0f / targetSampleRate;
		}
		
		@Override
		public double getSample(int idx) throws NoMoreDataException, InvalidAudioFormatException {
			float floatIdx = idx * scaleFactor;
			
			if (Math.abs(floatIdx - idx) < IDX_TOLERANCE) {
				// We're basically on top of an existing sample - just return it
				return originalChannel.getSample(idx);
			}

			// We're between two samples. Return their average.
			double precursor = originalChannel.getSample((int)floatIdx);
			double successor = originalChannel.getSample((int)Math.ceil(floatIdx));
			return (precursor + successor) / 2;
		}
		
		@Override
		public int getNumSamples() {
			return (int)(originalChannel.getNumSamples() / scaleFactor);
		}
	}
}
			
