package com.stefankopieczek.audinance.renderer;

import com.stefankopieczek.audinance.formats.DecodedAudio;

public abstract class Renderer 
{
	protected DecodedAudio mAudio;
	
	public Renderer(DecodedAudio decodedAudio)
	{
		mAudio = decodedAudio;
	}
	
	public abstract void play();
}
