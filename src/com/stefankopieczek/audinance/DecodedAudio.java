package com.stefankopieczek.audinance;
import java.io.*;
import android.widget.NumberPicker.*;

public class DecodedAudio
{
	private final AudioFormat mFormat;
	private final AudioSource[] mChannels;
	
	public DecodedAudio(AudioData audioData)
	{
		DecodedAudio temp = audioData.getDecodedAudio();
		mFormat = temp.getFormat();
		mChannels = temp.getChannels();
	}
	
	public DecodedAudio(AudioData audioData,
	                    AudioFormat format) 
		throws IOException, InvalidAudioFormatException
	{
		if (!format.isEntirelyDefined())
		{
			String err = "Underdefined format: " + format.toString();
			throw new InvalidAudioFormatException(err);
		}
	
		mFormat = format;
		DecodedAudio decodedAudio = audioData.getDecodedAudio();
		DecodedAudio convertedAudio = 
			AudioConverter.convert(decodedAudio, format);
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
}
