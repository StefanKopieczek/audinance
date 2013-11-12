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
	
	public EncodedAudio()
	{
		// Do-nothing constructor to allow subclasses to set themselves up if
		// desired.
	}
	
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
	
	public void writeToFile(File file) throws IOException	
	{
		FileOutputStream fos = new FileOutputStream(file);
		InputStream is = mData.getInputStream();
		byte[] buffer = new byte[65536];
		
		int len;
		try
		{
			while ((len = is.read(buffer)) != -1)
			{
				fos.write(buffer, 0, len);
			}
		}			
		finally
		{
			fos.close();
		}
	}
										
	protected EncodedSource getSource()
	{
		return mData;
	}
}
