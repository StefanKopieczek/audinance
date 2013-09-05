package com.stefankopieczek.audinance.formats;
import com.stefankopieczek.audinance.*;
import com.stefankopieczek.audinance.audiosources.*;
import java.io.*;

/** 
 * Generic representation of encoded audio data.
 */
public abstract class EncodedAudio
{
	private AudioSource mData;
	
	public EncodedAudio(File file) 
		throws FileNotFoundException, 
		       IOException, 
			   InvalidAudioFormatException
	{
		this(new FileInputStream(file));
	}
	
	public EncodedAudio(InputStream is) 
		throws IOException, InvalidAudioFormatException
	{
		mData = new SimpleAudioSource(is);
		parseData();
	}
	
	public EncodedAudio(EncodedAudio audioData, AudioFormat format) 
	{
		buildFromAudio(audioData, format);
	}
	
	public EncodedAudio(DecodedAudio rawAudioData,
	                 AudioFormat format)
	{
		buildFromAudio(rawAudioData, format);
	}
	
	public abstract InputStream getInputStream();
	
	public abstract DecodedAudio getDecodedAudio();
	
	public abstract DataType getDataType();
	
	public abstract void buildFromAudio(EncodedAudio audioData,
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
