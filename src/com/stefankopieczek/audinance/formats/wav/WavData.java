package com.stefankopieczek.audinance.formats.wav;
import com.stefankopieczek.audinance.formats.*;
import java.io.*;

public class WavData extends EncodedAudio
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
	
	public WavData(EncodedAudio encodedAudio, AudioFormat format) 
		throws IOException, InvalidAudioFormatException
	{
		super(encodedAudio, format);
	}

	public DecodedAudio getDecodedAudio() 
        throws InvalidWavDataException, UnsupportedWavEncodingException 
	{
		WavDecoder wavDecoder = new WavDecoder(getSource());
		return wavDecoder.getDecodedAudio();
		
	}

	public DataType getDataType()
	{
		// TODO: Implement this method
		return null;
	}
	
	@Override
	public void buildFromAudio(EncodedAudio encodedAudio,
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
