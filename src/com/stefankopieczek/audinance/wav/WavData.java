package com.stefankopieczek.audinance.wav;
import com.stefankopieczek.audinance.*;
import java.io.*;

public class WavData extends AudioData
{
	public WavData(File file)
		throws FileNotFoundException, 
		       IOException,
			   InvalidAudioFormatException
	{
		super(file);
	}
	
	public WavData(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		super(is);
	}
	
	public WavData(AudioData audioData) 
		throws IOException, InvalidAudioFormatException
	{
		super(audioData);
	}

	public InputStream getInputStream()
	{
		// TODO: Implement this method
		return null;
	}

	public RawAudioData getRawAudio()
	{
		// TODO: Implement this method
		return null;
	}

	public AudioFormat getFormat()
	{
		// TODO: Implement this method
		return null;
	}
}
