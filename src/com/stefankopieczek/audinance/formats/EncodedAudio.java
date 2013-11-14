package com.stefankopieczek.audinance.formats;
import com.stefankopieczek.audinance.audiosources.*;
import java.io.*;

/** 
 * Generic representation of encoded audio data.
 */
public abstract class EncodedAudio
{
	/**
	 * The data as read from the audio source, complete with any formatting
	 * information and compression.
	 */
	protected EncodedSource mData;
	
	public EncodedAudio()
	{
		// Do-nothing constructor to allow subclasses to set themselves up if
		// desired.
	}
	
	/**
	 * Creates an <tt>EncodedAudio</tt> object from the given file.
	 * This does not involve any audio processing; it simply sets up an
	 * <tt>EncodedSource</tt> to read from the file. 
	 * @param file The file to create the audio object from.
	 * @throws FileNotFoundException If the specified file does not exist.
	 * @throws IOException If we fail to read from the specified file.
	 */
	public EncodedAudio(File file) 
		throws FileNotFoundException, 
		       IOException
	{
		this(new FileInputStream(file));
	}
	
	/**
	 * Creates an <tt>EncodedAudio</tt> object from the given stream
	 * This does not involve any audio processing; it simply sets up an
	 * <tt>EncodedSource</tt> to read from the stream. 
	 * @param file The file to create the audio object from.
	 * @throws IOException If we fail to read from the stream.
	 */
	public EncodedAudio(InputStream is) 
		throws IOException
	{
		mData = new SimpleEncodedSource(is);
	}	
	
	/**
	 * Creates an <tt>EncodedAudio</tt> object from another encoded audio 
	 * object. This will usually involve decoding the source provided, and
	 * constructing a source for the target audio type inline, based upon
	 * the decoded data.
	 *  
	 * @param audioData The original data to be recoded in the format of
	 * this particular subclass of <tt>EncodedAudio</tt>. 
	 * @param format The desired format of this <tt>EncodedAudio</tt> object.
	 * @throws InvalidAudioFormatException If the format requested is invalid.
	 * @throws UnsupportedFormatException If we are unable to convert the given
	 * audio.
	 */
	public EncodedAudio(EncodedAudio audioData, AudioFormat format) 
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
		buildFromAudio(audioData, format);
	}
	
	/**
	 * Creates an <tt>EncodedAudio</tt> object from the decoded audio provided.
	 * This involves creating a source for the target audio type inline, 
	 * encoding the data provided.
	 * @param rawAudioData The original data to be recoded in the format of 
	 * this particular subclass of <tt>EncodedAudio</tt>.
	 * @param format The desired format of this <tt>EncodedAudio</tt> object.
	 * @throws InvalidAudioFormatException If the format requested is invalid. 
	 */
	public EncodedAudio(DecodedAudio rawAudioData,
	                    AudioFormat format) 
	    throws InvalidAudioFormatException
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
		throws InvalidAudioFormatException;
	
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
