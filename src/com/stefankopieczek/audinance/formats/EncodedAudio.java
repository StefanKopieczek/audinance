package com.stefankopieczek.audinance.formats;
import com.stefankopieczek.audinance.*;
import com.stefankopieczek.audinance.audiosources.*;
import java.io.*;

/** 
 * Generic representation of encoded audio data.
 */
public abstract class EncodedAudio
{
	protected EncodedSource mData;
	
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
		mData = new SimpleEncodedSource(is);
	}
	
	public EncodedAudio(EncodedAudio audioData, AudioFormat format) 
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
		buildFromAudio(audioData, format);
	}
	
	public EncodedAudio(DecodedAudio rawAudioData,
	                    AudioFormat format) 
	    throws InvalidAudioFormatException, UnsupportedFormatException
	{
		buildFromAudio(rawAudioData, format);
	}
	
	public abstract DecodedAudio getDecodedAudio() 
			throws InvalidAudioFormatException, UnsupportedFormatException;
	
	public abstract DataType getDataType();
	
	public abstract void buildFromAudio(EncodedAudio audioData,
	                                    AudioFormat format)
		throws InvalidAudioFormatException, UnsupportedFormatException;
	
	public abstract void buildFromAudio(DecodedAudio audioData,
	                                    AudioFormat format)
		throws InvalidAudioFormatException, UnsupportedFormatException;
	
	public abstract AudioFormat getFormat() 
        throws InvalidAudioFormatException, UnsupportedFormatException;
										
	protected EncodedSource getSource()
	{
		return mData;
	}
	
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
}
