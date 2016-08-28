package com.stefankopieczek.audinance.player;

import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;
import com.stefankopieczek.audinance.formats.wav.InvalidWavDataException;
import com.stefankopieczek.audinance.formats.wav.WavData;

import java.io.File;
import java.io.IOException;

/**
 * Created by stefan on 8/28/16.
 */
public final class FileLoader
{
    public static EncodedAudio loadAudio(File f) throws IOException
    {
        if (f.getName().endsWith(".wav"))
        {
            return new WavData(f);
        }
        else
        {
            // Speculatively try all the file types we know about.
            try
            {
                return new WavData(f);
            }
            catch (InvalidWavDataException e)
            {
                // Looks like this isn't a WAV file.
            }

            throw new UnsupportedFormatException("File " + f + " is corrupt, or in an unsupported format");
        }
    }
}
