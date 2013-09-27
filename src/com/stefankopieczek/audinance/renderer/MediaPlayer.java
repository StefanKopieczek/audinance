package com.stefankopieczek.audinance.renderer;

import com.stefankopieczek.audinance.formats.DecodedAudio;
import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.InvalidAudioFormatException;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;

public class MediaPlayer 
{
	public static void play(DecodedAudio audio)		
	{
		Renderer renderer = new JavaRenderer(audio);
		renderer.play();
	}
	
	public static void play(EncodedAudio audio)
		throws InvalidAudioFormatException, UnsupportedFormatException
	{
		play(audio.getDecodedAudio());
	}
}
