package com.kopieczek.audinance.conversion.multiplexers;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Basic implementation of multiplexer which:
 * <ul>
 * <li>Creates new channels by combining all existing streams.</li>
 * <li>When removing channels, flattens all surplus channels into a single 
 *     channel.</li>
 * </ul>
 * @author Stefan Kopieczek
 *
 */
public class SimpleMultiplexer implements Multiplexer
{
    private static final Logger sLogger = Logger.getLogger(SimpleMultiplexer.class.getName());

    /**
	 * Remix the specified audio, returning a copy with precisely the specified
	 * number of channels. If it has too few, we add channels equal to the mix
	 * of all existing channels. If it has too many, we flatten the last few
	 * channels into a single track.  
	 */
	public DecodedAudio toNChannels(DecodedAudio originalAudio,
                                    Integer targetNumChannels)
	{
		DecodedSource[] oldChannels = originalAudio.getChannels();
        sLogger.info("Remultiplexing " + originalAudio + " from " + oldChannels.length + " channels to " +
                     targetNumChannels);

        DecodedSource[] newChannels =
	  	              new DecodedSource[targetNumChannels.intValue()];
		
		if (targetNumChannels > oldChannels.length)
		{
			// More channels have been requested than currently exist.
			
			// Copy the existing channels unchanged.
			for (int idx = 0; idx < oldChannels.length; idx++)
			{
				newChannels[idx] = oldChannels[idx];
			}
			
			// Make up the total by adding channels which are simply a 
			// flattened copy of all existing channels.
			for (int idx = oldChannels.length; idx < targetNumChannels; idx++)
			{
				newChannels[idx] = new CombinedAudio(oldChannels);
			}							
		}
		else
		{
			// Fewer channels have been requested than currently exist.
			// We don't want to lose audio data, so instead of dropping the
			// surplus channels, we flatten them into a single channel.
			
			// Copy the first n-1 channels unchanged.
			for (int idx = 0; idx < targetNumChannels - 1; idx++)
			{
				newChannels[idx] = oldChannels[idx];
			}
		
			// Flatten all remaining channels into a single track, and add it.
			newChannels[targetNumChannels-1] =
				new CombinedAudio(Arrays.copyOfRange(oldChannels,
						                             targetNumChannels - 1,
						                             oldChannels.length));
		}
		
		DecodedAudio newAudio = new DecodedAudio(originalAudio.getFormat(),
                                                 newChannels);
		return newAudio;
	}
	
	private class CombinedAudio extends DecodedSource
	{
		DecodedSource[] mSources;
		
		public CombinedAudio(DecodedSource... sources)
		{
			mSources = sources;
		}
		
		public double getSample(int idx) throws InvalidAudioFormatException
		{
			double sampleValue = 0;
			boolean hasData = false;

			// Use an iterative mean algorithm to take the average of all channel values at this index, without risking
			// overflowing
			int t = 1;
			for (DecodedSource source : mSources)
			{
				try
				{
					sampleValue += (source.getSample(idx) - sampleValue) / t;
					t++;
					hasData = true;
				}
				catch (NoMoreDataException e)
				{
					// This source has no data at this instant, so makes no
					// contribution to the mixed audio.
				}							
			}
			
			if (hasData)
			{
				return sampleValue;
			}
			else
			{
				throw new NoMoreDataException();
			}
		}
		
		@Override
		public int getNumSamples()
		{
			int result = 0;

			for (DecodedSource source : mSources)
			{
				int sourceSamples = source.getNumSamples();
				
				if (sourceSamples > result)
				{
					result = sourceSamples;
				}
			}
			
			return result;
		}
	}
}
