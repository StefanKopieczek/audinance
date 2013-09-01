package com.stefankopieczek.audinance;
import java.io.*;

public abstract class AudioData
{
	private AudioSource mData;
	
	public AudioData(File file) 
		throws FileNotFoundException, 
		       IOException, 
			   InvalidAudioFormatException
	{
		this(new FileInputStream(file));
	}
	
	public AudioData(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		mData = new SimpleAudioSource(is);
		parseData();
	}
	
	public AudioData(AudioData audioData, AudioFormat format) 
	{
		buildFromAudio(audioData, format);
	}
	
	public AudioData(DecodedAudio rawAudioData,
	                 AudioFormat format)
	{
		buildFromAudio(rawAudioData, format);
	}
	
	public abstract InputStream getInputStream();
	
	public abstract DecodedAudio getDecodedAudio();
	
	public abstract DataType getDataType();
	
	public abstract void buildFromAudio(AudioData audioData,
	                                    AudioFormat format);
	
	public abstract void buildFromAudio(DecodedAudio audioData,
	                                    AudioFormat format);
	
	protected class AudioDataStream extends InputStream
	{
		private int position;
		
		public AudioDataStream() {}
		
		@Override
		public int read()
		{
			int result;
			try
			{
				result = mData.getByte(position++);
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				result = -1;
			}
			
			return result;
		}
	}
	
	private void parseData() throws InvalidAudioFormatException
	{
		if (!isDataValid())
		{
			throw new InvalidAudioFormatException(getDataType());
		}
	}
	
	protected boolean isDataValid()
	{
		// Override this!
		throw new UnsupportedOperationException();
	}
}
