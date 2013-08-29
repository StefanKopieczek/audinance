package com.stefankopieczek.audinance;
import java.io.*;

public abstract class AudioData
{
	private byte[] mData;
	
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
		mData = AudinanceUtils.getByteArrayFromStream(is);
		validateData();
	}
	
	public AudioData(AudioData audioData) 
		throws IOException, InvalidAudioFormatException
	{
		this(audioData.getRawAudio().getInputStream());
	}
	
	public abstract InputStream getInputStream();
	
	public abstract RawAudioData getRawAudio();
	
	public abstract AudioFormat getFormat();
	
	protected class AudioDataStream extends InputStream
	{
		private int position;
		
		public AudioDataStream()
		{
		}
		
		@Override
		public int read()
		{
			int result;
			if (position < mData.length)
			{
				result = mData[position++];
			}
			else
			{
				result = -1;
			}
			
			return result;
		}
	}
	
	private void validateData() throws InvalidAudioFormatException
	{
		if (!isDataValid())
		{
			throw new InvalidAudioFormatException(getFormat());
		}
	}
	
	protected boolean isDataValid()
	{
		// Override this!
		throw new UnsupportedOperationException();
	}
}
