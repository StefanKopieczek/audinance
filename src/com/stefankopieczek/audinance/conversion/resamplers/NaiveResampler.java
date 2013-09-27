package com.stefankopieczek.audinance.conversion.resamplers;
import com.stefankopieczek.audinance.formats.*;
import com.stefankopieczek.audinance.audiosources.*;

public class NaiveResampler implements Resampler
{
	public DecodedAudio resample(DecodedAudio original, 
	                             Integer targetSampleRate)
	{
		DecodedSource[] originalChannels = 
		                                       original.getChannels();
		AudioFormat originalFormat = original.getFormat();
		Integer originalSampleRate = originalFormat.getSampleRate();

		AudioFormat newFormat = 
			new AudioFormat(targetSampleRate, 
			                originalFormat.getNumChannels());
		
		DecodedSource[] newChannels = 
	                	   new DecodedSource[originalChannels.length];
		
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
	
	class ResamplingSource extends DecodedSource
	{
		private final DecodedSource mOriginal;
		private final float mScaleFactor;
		private static final float IDX_TOLERANCE = 0.0001f;
		
		public ResamplingSource(DecodedSource original,
		                        int originalSampleRate,
								int targetSampleRate)
		{
			mOriginal = original;
			mScaleFactor = originalSampleRate * 1.0f / targetSampleRate;			
		}
		
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
	}
}
			
