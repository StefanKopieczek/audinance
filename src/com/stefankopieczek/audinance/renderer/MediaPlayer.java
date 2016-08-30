package com.stefankopieczek.audinance.renderer;

import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;

import java.util.logging.Logger;

public class MediaPlayer 
{
	private static final Logger sLogger = Logger.getLogger(MediaPlayer.class.getName());

	public static void play(DecodedAudio audio)		
	{
	    sLogger.info("Playing encoded audio: " + audio);
		Renderer renderer = new JavaRenderer(audio);
		renderer.play();
	}
	
	public static void play(EncodedAudio audio)
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
	    sLogger.info("Playing decoded audio: " + audio);
		play(audio.getDecodedAudio());
	}
}
