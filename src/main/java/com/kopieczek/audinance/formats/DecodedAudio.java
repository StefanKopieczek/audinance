package com.kopieczek.audinance.formats;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.conversion.multiplexers.Multiplexer;
import com.kopieczek.audinance.conversion.multiplexers.SimpleMultiplexer;
import com.kopieczek.audinance.conversion.resamplers.NaiveResampler;
import com.kopieczek.audinance.conversion.resamplers.Resampler;

import java.util.logging.Logger;


/**
 * Raw audio data decoded into memory, not stored as any particular
 * data type.
 */
public class DecodedAudio
{
    private static final Logger sLogger = Logger.getLogger(DecodedAudio.class.getName());

    /**
	 * The format of the audio.
	 */
	private final AudioFormat mFormat;
	
	/**
	 * The raw audio data, each channel with its own source.
	 */
	private final DecodedSource[] mChannels;
	
	public DecodedAudio(DecodedSource[] channels, AudioFormat format)
	{
		mChannels = channels;
		mFormat = format;
	}
	
	public DecodedAudio(AudioFormat format, DecodedSource... channels)
	{
		mChannels = channels;
		mFormat = format;
	}
	
	public DecodedAudio(EncodedAudio encodedAudio)
			throws InvalidAudioFormatException, UnsupportedFormatException
	{
		DecodedAudio temp = encodedAudio.getDecodedAudio();
		mFormat = temp.getFormat();
		mChannels = temp.getChannels();
	}
	
	public DecodedAudio(EncodedAudio encodedAudio,
	                    AudioFormat format) 
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
		if (!format.isEntirelyDetermined())
		{
			String err = "Underdefined format: " + format.toString();
			throw new InvalidAudioFormatException(err);
		}
	
		mFormat = format;
		DecodedAudio decodedAudio = encodedAudio.getDecodedAudio();
		DecodedAudio convertedAudio = decodedAudio.convertTo(format);
		mChannels = convertedAudio.getChannels();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof DecodedAudio))
			return false;
					
		DecodedAudio otherAudio = (DecodedAudio)other;
		
		// Formats are unequal; so audio objects are not equal.
		if (!(mFormat.equals(otherAudio.mFormat)))
			return false;
		
		// Number of channels are different; so audio objects are unequal.
		if (mChannels.length != otherAudio.getChannels().length)
			return false;
		
		for (int idx = 0; idx < mChannels.length; idx++)
		{
			// If two channels are unequal, the audio objects are unequal.
			if (!(mChannels[idx].equals(otherAudio.getChannels()[idx])))
				return false;
		}
		
		return true;
	}

	public AudioFormat getFormat()
	{
		return mFormat;
	}
	
	public DecodedSource[] getChannels()
	{
		return mChannels;
	}
	
	/**
	 * Returns a copy of this audio in the target format.
	 * Handles resampling, multiplexing and demultiplexing as needed.
	 *
	 * @param targetFormat The format to convert to.
	 * @return The original audio, converted into the new format.
	 */
	public DecodedAudio convertTo(AudioFormat targetFormat)
	{
		sLogger.info("Converting " + this + " to format " + targetFormat);
		DecodedAudio result = this;
		
		// Resample the audio if required.
		// (<tt>null</tt> indicates no requirement)
		Integer targetSampleRate = targetFormat.getSampleRate();
		if (targetSampleRate != null && 
		    !targetSampleRate.equals(mFormat.getSampleRate()))
		{
		    sLogger.fine("Resampling " + this + " from " + mFormat.getSampleRate() + " to " + targetSampleRate);
			Resampler resampler = new NaiveResampler();
			result = resampler.resample(result, targetSampleRate);
		}
		
		// Multiplex or demultiplex  the audio if required.
		// (<tt>null</tt> indicates no requirement)
		Integer targetNumChannels = targetFormat.getNumChannels();
		if (targetNumChannels != null &&
		    !targetNumChannels.equals(mFormat.getNumChannels()))
		{
		    sLogger.fine("Remultiplexing " + this + " from " + mFormat.getNumChannels() + " channels to " +
                         targetNumChannels);
			Multiplexer multiplexer = new SimpleMultiplexer();
			result = multiplexer.toNChannels(result, 
			                                 targetNumChannels);
		}
		
		return result;
	}	
	
	/**
	 * Gets an array of arrays, each containing the raw sample data of a single
	 * channel.
	 * 
	 * @return The array of sample data arrays.
	 * @throws InvalidAudioFormatException If the audio's format data is 
	 * invalid.
	 */
	public double[][] getRawData() throws InvalidAudioFormatException
	{
		double[][] result = new double[mChannels.length][];
		
		for (int channelIdx = 0; channelIdx < mChannels.length; channelIdx++)
		{
			result[channelIdx] = mChannels[channelIdx].getRawData();
		}			
		
		return result;
	}

	@Override
    public String toString()
    {
        return "<DecodedAudio - " + mFormat + ">";
    }
}
