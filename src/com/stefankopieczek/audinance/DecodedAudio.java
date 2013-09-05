package com.stefankopieczek.audinance;
import com.stefankopieczek.audinance.multiplexer.*;
import com.stefankopieczek.audinance.resampler.*;
import java.io.*;

/**
 * Raw audio data decoded into memory, not stored as any particular
 * data type.
 */
public class DecodedAudio
{
	private final AudioFormat mFormat;
	private final AudioSource[] mChannels;
	
	public DecodedAudio(EncodedAudio encodedAudio)
	{
		DecodedAudio temp = encodedAudio.getDecodedAudio();
		mFormat = temp.getFormat();
		mChannels = temp.getChannels();
	}
	
	public DecodedAudio(EncodedAudio encodedAudio,
	                    AudioFormat format) 
		throws IOException, InvalidAudioFormatException
	{
		if (!format.isEntirelyDetermined())
		{
			String err = "Underdefined format: " + format.toString();
			throw new InvalidAudioFormatException(err);
		}
	
		mFormat = format;
		DecodedAudio decodedAudio = encodedAudio.getDecodedAudio();
		DecodedAudio convertedAudio = convertTo(format);
		mChannels = convertedAudio.getChannels();
	}

	public AudioFormat getFormat()
	{
		return mFormat;
	}
	
	public AudioSource[] getChannels()
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
		DecodedAudio result = this;
		
		// Resample the audio if required.
		// (<tt>null</tt> indicates no requirement)
		Integer targetSampleRate = targetFormat.getSampleRate();
		if (targetSampleRate != null && 
		    !targetSampleRate.equals(mFormat.getSampleRate()))
		{
			Resampler resampler = new SimpleResampler();
			result = resampler.resample(result, targetSampleRate);
		}
		    
		// Rewrite the audio at a new bitdepth if required.
		// (<tt>null</tt> indicates no requirement)	
		Integer targetBitDepth = targetFormat.getBitDepth();
		if (targetBitDepth != null &&
		    !targetBitDepth.equals(mFormat.getSampleRate()))
		{
			result = result.convertToNewBitDepth(targetBitDepth);
		}
		
		// Multiplex or demultiplex  the audio if required.
		// (<tt>null</tt> indicates no requirement)
		Integer targetNumChannels = targetFormat.getNumChannels();
		if (targetNumChannels != null &&
		    !targetNumChannels.equals(mFormat.getNumChannels()))
		{
			Multiplexer multiplexer = new SimpleMultiplexer();
			result = multiplexer.toNChannels(result, 
			                                 targetNumChannels);
		}
		
		return result;
	}

	public DecodedAudio convertToNewBitDepth(Integer targetBitDepth)
	{
		// TODO: Implement this method
		return null;
	}
}
