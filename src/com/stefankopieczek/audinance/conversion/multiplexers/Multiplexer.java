package com.stefankopieczek.audinance.conversion.multiplexers;

import com.stefankopieczek.audinance.formats.DecodedAudio;

/**
 * Takes decoded audio with a given number of channels and returns audio with
 * a different number of channels.
 * @author Stefan Kopieczek
 *
 */
public interface Multiplexer
{
	/**
	 * Remultiplexes <tt>DecodedAudio</tt> data to have the specified number
	 * of channels, adding or removing streams as required, but trying to
	 * maintain as close a similarity to the original as possible.
	 * The original data is not modified.
	 * It is up to the implementation to decide how to preserve the data from
	 * removed streams and how to fill out new streams added to meet the total.
	 * @param originalAudio The original audio data to remultiplex.
	 * @param targetNumChannels The number of channels desired in the output.
	 * @return <tt>DecodedAudio</tt> data that sounds as close as possible to
	 * the original, but has the specified number of channels.
	 */
	public abstract DecodedAudio toNChannels(DecodedAudio originalAudio, 
	                                         Integer targetNumChannels);
}
