package com.stefankopieczek.audinance.player;

import com.stefankopieczek.audinance.formats.EncodedAudio;
import com.stefankopieczek.audinance.formats.UnsupportedFormatException;
import com.stefankopieczek.audinance.formats.wav.InvalidWavDataException;
import com.stefankopieczek.audinance.formats.wav.WavData;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by stefan on 8/28/16.
 */
public final class FileLoader
{
    private static final Logger sLogger = Logger.getLogger(FileLoader.class.getName());

    public static EncodedAudio loadAudio(File f) throws IOException
    {
        sLogger.fine("Determining audio format for " + f);

        if (f.getName().endsWith(".wav"))
        {
            sLogger.fine("Decided " + f + " is WAV-encoded, based on extension");
            return new WavData(f);
        }
        else
        {
            // Speculatively try all the file types we know about.
            try
            {
                sLogger.finest("Trying to decode " + f + " as WAV...");
                WavData wav = new WavData(f);
                wav.getDecodedAudio();
                sLogger.fine("Successfully parsed " + f + " as a WAV file");
                return wav;
            }
            catch (InvalidWavDataException e)
            {
                // Looks like this isn't a WAV file.
                sLogger.finest("Failed to decode " + f + " as WAV");
            }

            throw new UnsupportedFormatException("File " + f + " is corrupt, or in an unsupported format");
        }
    }
}
