package com.kopieczek.audinance.renderer;

import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import com.kopieczek.audinance.utils.BitUtils;
import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.audiosources.NoMoreDataException;
import com.kopieczek.audinance.formats.DecodedAudio;

public class BasicRenderer extends Renderer
{
    private static final Logger sLogger = Logger.getLogger(BasicRenderer.class.getName());

	public BasicRenderer(DecodedAudio decodedAudio)
	{
		super(decodedAudio);
	}
	
	public void play()
	{
	    sLogger.info("Playing decoded audio " + mAudio);

		javax.sound.sampled.AudioFormat format = mAudio.getFormat().getJmfAudioFormat();
		SourceDataLine line = null;
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		if (!AudioSystem.isLineSupported(info))
		{
			sLogger.warning("Audio " + mAudio + " is in a format that Java cannot natively play: " + info);
		}
		try
		{
			line = (SourceDataLine)AudioSystem.getLine(info);
			line.open(format);
		}
		catch (LineUnavailableException ex)
		{
			sLogger.log(Level.SEVERE, "Line unavailable while playing audio", ex);
		}
		
		new LineWriter(line).start();
	}
	
	private class LineWriter extends Thread
	{
		private SourceDataLine mLine;
		
		public LineWriter(SourceDataLine line)
		{
			mLine = line;
		}
		
		@Override
		public void run()
		{
		    sLogger.fine("Starting LineWriter " + this);
			mLine.start();
			
			try
			{
				writeAudio();
			}
			catch (InvalidAudioFormatException e)
			{
				sLogger.log(Level.SEVERE, mAudio + " is in an invalid format", e);
			}
			finally
			{
			    sLogger.fine("Closing LineWriter");
				mLine.drain();
				mLine.stop();
				mLine.close();
				mLine = null;
			}
		}			
		
		private void writeAudio() throws InvalidAudioFormatException
		{
			boolean hasData = true;
			int ptr = 0;			
			int frameSize = mAudio.getFormat().getNumChannels() * 2;
			while (hasData)
			{
				hasData = false;
				byte[] frame = new byte[frameSize];
				for (int idx = 0; idx < mAudio.getChannels().length; idx++)
				{					
					DecodedSource channel = mAudio.getChannels()[idx];	
					byte[] sampleBytes;
					try
					{						
						short sample = (short)channel.getSample(ptr);
						sampleBytes = BitUtils.bytesFromShort(
								              sample, ByteOrder.LITTLE_ENDIAN);
						hasData = true;
					}
					catch (NoMoreDataException e)
					{
						// Just write blank data.
						// If all channels are out of data, we stop altogether.
						sampleBytes = new byte[2];
					}		
					System.arraycopy(sampleBytes,  0, frame, 2 * idx, 2);
				}
				
				mLine.write(frame, 0, frameSize);
				ptr++;		
			}
		}
	}
}
