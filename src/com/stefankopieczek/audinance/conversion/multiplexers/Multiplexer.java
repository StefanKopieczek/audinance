package com.stefankopieczek.audinance.conversion.multiplexers;
import com.stefankopieczek.audinance.formats.*;

public interface Multiplexer
{
	public DecodedAudio toNChannels(DecodedAudio result, 
	                                Integer targetNumChannels);
}
