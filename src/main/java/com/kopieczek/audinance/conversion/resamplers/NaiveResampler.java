package com.kopieczek.audinance.conversion.resamplers;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;

import java.util.logging.Logger;

/**
 * A <tt>Resampler</tt> that approximates points between known samples by
 * taking an (unweighted) average of its immediate neighbours.
 * This provides fast, "good enough" resampling, but is not as accurate as
 * polynomial interpolation.
 * It would be dramatically improved at little expense by using a weighted
 * average, but I'm tired and I need to write a ton more comments first.
 * 
 * @author Stefan Kopieczek
 *
 */
public class NaiveResampler implements Resampler
{
    private static final Logger sLogger = Logger.getLogger(NaiveResampler.class.getName());

	public DecodedAudio resample(DecodedAudio original,
                                 Integer targetSampleRate)
	{
		DecodedSource[] originalChannels = original.getChannels();
		AudioFormat originalFormat = original.getFormat();
		Integer originalSampleRate = originalFormat.getSampleRate();
        sLogger.info("Resampling " + original + " from " + originalSampleRate + "Hz to " +
                     targetSampleRate + "Hz");

		AudioFormat newFormat = 
			new AudioFormat(targetSampleRate, 
			                originalFormat.getNumChannels());
		
		DecodedSource[] newChannels = 
	                	   new DecodedSource[originalChannels.length];
	
		// Build a new DecodedAudio object composed of resampled sources at the
		// new frequency.
		for (int ii = 0; ii < newChannels.length; ii++)
		{
			newChannels[ii] = new ResamplingSource(originalChannels[ii],
			                                       originalSampleRate.intValue(),
												   targetSampleRate.intValue());
		}
		
		DecodedAudio result = new DecodedAudio(newChannels,
		                                       newFormat);
											   
		return result;
	}
	
	/**
	 * A <tt>DecodedSource</tt> that wraps another <tt>DecodedSource</tt> in
	 * order to provide samples at the desired rate; that is, it is a 
	 * resampling source for a single channel of audio.
	 * It approximates samples that do not line up with the original data by
	 * taking an unweighted average of the two immediate neighbour points.
	 * 
	 * @author Stefan Kopieczek
	 *
	 */
	private class ResamplingSource extends DecodedSource
	{
		/**
		 * The original audio data.
		 */
		private final DecodedSource mOriginal;
		
		/**
		 * The factor by which the sample rate is to be divided to get from the
		 * original to the target.
		 */
		private final float mScaleFactor;
		
		/**
		 * The tolerance we use for assessing equality of doubles.
		 * This is used to judge if two instants in time are equal, so that we
		 * can re-use points in the original data that are close to the desired
		 * sample points.
		 */
		private static final float IDX_TOLERANCE = 0.0001f;
		
		public ResamplingSource(DecodedSource original,
		                        int originalSampleRate,
								int targetSampleRate)
		{
			mOriginal = original;
			mScaleFactor = originalSampleRate * 1.0f / targetSampleRate;			
		}
		
		@Override
		public double getSample(int idx) 
				throws NoMoreDataException, InvalidAudioFormatException
		{
			float floatIdx = idx * mScaleFactor;
			
			if (Math.abs(floatIdx - idx) < IDX_TOLERANCE)
			{
				return mOriginal.getSample(idx);
			}
			
			double precursor = mOriginal.getSample((int)floatIdx);
			double successor = mOriginal.getSample((int)Math.ceil(floatIdx));
			
			return (precursor+successor)/2;
		}
		
		@Override
		public int getNumSamples()
		{
			return (int)(mOriginal.getNumSamples() / mScaleFactor);
		}
	}
}
			
