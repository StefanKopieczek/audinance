package com.stefankopieczek.audinance.formats;
import com.stefankopieczek.audinance.*;
import com.stefankopieczek.audinance.audiosources.*;
import com.stefankopieczek.audinance.conversion.multiplexers.*;
import com.stefankopieczek.audinance.conversion.resamplers.*;
import java.io.*;

/**
 * Raw audio data decoded into memory, not stored as any particular
 * data type.
 */
public class DecodedAudio
{
	private final AudioFormat mFormat;
	public final DecodedSource[] mChannels;
	
	public DecodedAudio(DecodedSource[] channels, AudioFormat format)
	{
		mChannels = channels;
		mFormat = format;
	}
	
	public DecodedAudio(EncodedAudio encodedAudio)
	{
		DecodedAudio temp = encodedAudio.getDecodedAudio();
		mFormat = temp.getFormat();
		mChannels = temp.getChannels();
	}
	
	public DecodedAudio(EncodedAudio encodedAudio,
	                    AudioFormat format) 
		throws InvalidAudioFormatException
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
		DecodedAudio result = this;
		
		// Resample the audio if required.
		// (<tt>null</tt> indicates no requirement)
		Integer targetSampleRate = targetFormat.getSampleRate();
		if (targetSampleRate != null && 
		    !targetSampleRate.equals(mFormat.getSampleRate()))
		{
			Resampler resampler = new NaiveResampler();
			result = resampler.resample(result, targetSampleRate);
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
}
