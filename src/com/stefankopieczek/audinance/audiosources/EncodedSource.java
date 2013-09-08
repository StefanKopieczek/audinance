package com.stefankopieczek.audinance.audiosources;

/**
 * Abstraction over an audio data source. Allows the implementation
 * to decide the best way to access the underlying data.
 */
public abstract class EncodedSource
{
 	/**
	 * Read a byte from the data source at the given index.
	 **/
	public abstract byte getByte(int index);
}