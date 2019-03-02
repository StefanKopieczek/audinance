package com.kopieczek.audinance.renderer;

import com.kopieczek.audinance.formats.DecodedAudio;

public abstract class Renderer 
{
	protected DecodedAudio mAudio;
	
	public Renderer(DecodedAudio decodedAudio)
	{
		mAudio = decodedAudio;
	}
	
	public abstract void play();
}
