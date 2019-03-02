package com.kopieczek.audinance.renderer;

import com.kopieczek.audinance.formats.EncodedAudio;
import com.kopieczek.audinance.formats.InvalidAudioFormatException;
import com.kopieczek.audinance.formats.UnsupportedFormatException;
import com.kopieczek.audinance.formats.DecodedAudio;

import java.util.logging.Logger;

public class MediaPlayer 
{
	private static final Logger sLogger = Logger.getLogger(MediaPlayer.class.getName());

	public static void play(DecodedAudio audio)		
	{
	    sLogger.info("Playing encoded audio: " + audio);
		Renderer renderer = new RemixingRenderer(audio);
		renderer.play();
	}
	
	public static void play(EncodedAudio audio)
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
	    sLogger.info("Playing decoded audio: " + audio);
		play(audio.getDecodedAudio());
	}
}
