package com.kopieczek.audinance.renderer;

import com.kopieczek.audinance.conversion.multiplexers.Multiplexer;
import com.kopieczek.audinance.conversion.multiplexers.SimpleMultiplexer;
import com.kopieczek.audinance.conversion.resamplers.NaiveResampler;
import com.kopieczek.audinance.conversion.resamplers.Resampler;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;

import java.util.logging.Logger;

public class RemixingRenderer extends BasicRenderer
{
    private static final Logger sLogger = Logger.getLogger(RemixingRenderer.class.getName());

    private static final int MIN_SAMPLE_RATE_HZ = 8000;
    private static final int MAX_SAMPLE_RATE_HZ = 48000;
    private static final int MAX_CHANNELS = 6;

    public RemixingRenderer(DecodedAudio audio)
    {
        super(convertToSupportedFormat(audio));
    }

    private static DecodedAudio convertToSupportedFormat(DecodedAudio audio)
    {
        AudioFormat format = audio.getFormat();

        // Decide whether we need to resample the audio in order for javax.sound to play it.
        int targetSampleRate = format.getSampleRate();
        if (format.getSampleRate() < MIN_SAMPLE_RATE_HZ)
        {
            sLogger.fine(audio + ": sample rate " + format.getSampleRate() + " is too low for javax.sound playback. " +
                         "Upsampling to " + MIN_SAMPLE_RATE_HZ);
            targetSampleRate = MIN_SAMPLE_RATE_HZ;
        }
        else if (format.getSampleRate() > MAX_SAMPLE_RATE_HZ)
        {
            sLogger.fine(audio + ": sample rate " + format.getSampleRate() + " is too high for javax.sound playback. " +
                         "Downsampling to " + MAX_SAMPLE_RATE_HZ);
            targetSampleRate = MAX_SAMPLE_RATE_HZ;
        }
        else
        {
            sLogger.fine(audio + ": sample rate " + format.getSampleRate() + " looks fine for javax.sound playback");
        }

        if (targetSampleRate != format.getSampleRate())
        {
            // Resampling is required.
            Resampler resampler = new NaiveResampler();
            audio = resampler.resample(audio, targetSampleRate);
        }

        if (format.getNumChannels() > MAX_CHANNELS)
        {
            sLogger.fine(audio + " has too many channels channels (" + format.getNumChannels() +
                         ") for javax.sound playback. Demultiplexing to " + MAX_CHANNELS);
            Multiplexer multiplexer = new SimpleMultiplexer();
            audio = multiplexer.toNChannels(audio, MAX_CHANNELS);
        }
        else
        {
            sLogger.fine(audio + ": number of channels (" + format.getNumChannels() +
                         ") looks fine for javax.sound playback");
        }

        return audio;
    }
}
