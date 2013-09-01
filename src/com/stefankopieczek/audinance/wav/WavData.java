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
	
	public WavData(AudioData audioData, AudioFormat format) 
		throws IOException, InvalidAudioFormatException
	{
		super(audioData, format);
	}

	public InputStream getInputStream()
	{
		// TODO: Implement this method
		return null;
	}

	public DecodedAudio getDecodedAudio()
	{
		// TODO: Implement this method
		return null;
	}

	public DataType getDataType()
	{
		// TODO: Implement this method
		return null;
	}
	
	@Override
	public void buildFromAudio(AudioData audioData,
	                             AudioFormat format)
	{
		// todo
	}
	
	@Override
	public void buildFromAudio(DecodedAudio rawAudioData,
	                           AudioFormat audioFormat)
	{
		// todo
	}
}
