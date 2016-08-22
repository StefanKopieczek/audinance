package com.stefankopieczek.audinance.formats;
import com.stefankopieczek.audinance.audiosources.EncodedSource;
import com.stefankopieczek.audinance.audiosources.SimpleEncodedSource;

import java.io.*;
import java.util.logging.Logger;

/** 
 * Generic representation of encoded audio data.
 */
public abstract class EncodedAudio
{
    private static final Logger sLogger = Logger.getLogger(EncodedAudio.class.getName());

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
	 * @param is The <tt>InputStream</tt> to create the audio object from.
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
	
	/**
	 * Decodes the <tt>EncodedAudio</tt> object into a <tt>DecodedAudio</tt>
	 * object. Decoding may occur up front, or 'just in time' as data is
	 * requested.
	 * @return The raw audio represented by this encoded data.
	 * @throws InvalidAudioFormatException if the format object associated
	 *         with this <tt>EncodedAudio</tt> is underdetermined.
	 * @throws UnsupportedFormatException TODO SMK
	 */
	public abstract DecodedAudio getDecodedAudio() 
			throws InvalidAudioFormatException, UnsupportedFormatException;
	
	/**
	 * Gets the data type of this encoded audio; e.g. WAV or MP3.
	 */
	public abstract DataType getDataType();
	
	/**
	 * Populates this <tt>EncodedAudio</tt> object by decoding the specified
	 * encoded audio and recoding it in the format associated with this class.
	 *
	 * This method should only be called from within its class's constructor.
	 *
	 * @param audioData The audio to re-encode. This need not be of the same
	 *                  datatype as the class from which this method is invoked.
	 * @param format The recording format (sample rate, etc) with which the new
	 *               audio should be saved.
	 * @throws InvalidAudioFormatException TODO SMK
	 * @throws UnsupportedFormatException TODO SMK
	 *
	 */
	protected abstract void buildFromAudio(EncodedAudio audioData,
	                                       AudioFormat format)
		throws InvalidAudioFormatException, UnsupportedFormatException;
		
	/**
	 * Populates this <tt>EncodedAudio</tt> object by encoding the given
	 * <tt>DecodedAudio</tt> in the format associated with this class.
	 *
	 * This method should only be called from within its class's constructor.
	 *
	 * @param audioData The audio to encode.
	 * @param format The recording format (sample rate, etc) with which the new
	 *               audio should be saved.
	 * @throws InvalidAudioFormatException TODO SMK
	 *
	 */
	protected abstract void buildFromAudio(DecodedAudio audioData,
	                                       AudioFormat format)
		throws InvalidAudioFormatException;
	
	/**
	 * Gets the recording format of this encoded audio.
	 *
	 * @return The audio format.
	 * @throws InvalidAudioFormatException TODO SMK
	 * @throws UnsupportedFormatException TODO SMK
	 */
	public abstract AudioFormat getFormat() 
        throws InvalidAudioFormatException, UnsupportedFormatException;
	
	/**
	 * Stores this encoded audio in the given file, overwriting any previous
	 * contents. This creates a valid media file of the type associated with
	 * this subclass of <tt>EncodedAudio</tt>.
	 *
	 * @param file The file to write to.
	 * @throws IOException If the file cannot be written to.
	 */
	public void writeToFile(File file) throws IOException	
	{
		sLogger.info("Persisting " + this + " to " + file);
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
		
    /**
	 * Returns the encoded data associated with this audio, complete with
	 * format headers and any other metadata.
	 *
	 * @return The data source.
	 */
	protected EncodedSource getSource()
	{
		return mData;
	}
}
