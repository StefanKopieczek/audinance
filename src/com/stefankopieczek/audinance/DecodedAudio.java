package com.stefankopieczek.audinance;
import java.io.*;
import android.widget.NumberPicker.*;

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
		if (!format.isEntirelyDefined())
		{
			String err = "Underdefined format: " + format.toString();
			throw new InvalidAudioFormatException(err);
		}
	
		mFormat = format;
		DecodedAudio decodedAudio = encodedAudio.getDecodedAudio();
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
