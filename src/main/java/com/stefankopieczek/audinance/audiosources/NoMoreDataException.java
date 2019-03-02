package com.stefankopieczek.audinance.audiosources;

/**
 * Thrown when trying to access audio data from a data source if audio data is
 * not available at a given sample index, because the audio has already 
 * finished by that point. 
 * @author Stefan Kopieczek
 *
 */
public class NoMoreDataException extends RuntimeException 
{
}
