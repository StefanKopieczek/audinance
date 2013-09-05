package com.stefankopieczek.audinance.multiplexer;
import com.stefankopieczek.audinance.*;

public interface Multiplexer
{

	public DecodedAudio toNChannels(DecodedAudio result, Integer targetNumChannels);

}
