package com.stefankopieczek.audinance.formats.flac.structure;

/**
 * Created by stefan on 9/1/16.
 */
public class UnsupportedFlacDataException extends RuntimeException
{
    public UnsupportedFlacDataException(String message)
    {
        super(message);
    }
}
