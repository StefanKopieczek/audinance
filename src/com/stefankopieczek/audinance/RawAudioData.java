package com.stefankopieczek.audinance;
import java.io.*;

public class RawAudioData extends AudioData
{
	private byte[] mData;
	private static final AudioFormat mFormat = AudioFormat.RAW;
	
	public RawAudioData(File file)
		throws FileNotFoundException, 
		       IOException,
			   InvalidAudioFormatException
	{
		super(file);
		throw new UnsupportedOperationException();
	}
	
	public RawAudioData(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		super(is);
	}
	
	public RawAudioData(AudioData audioData) 
		throws IOException, InvalidAudioFormatException
	{
		super(audioData);
	}
	
	@Override
	public InputStream getInputStream()
	{
		return new AudioDataStream();
	}

	@Override
	public RawAudioData getRawAudio()
	{
		return this;
	}

	@Override
	public AudioFormat getFormat()
	{
		return mFormat;
	}
}
