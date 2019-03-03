package com.kopieczek.audinance.conversion;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.testutils.MockDecodedSource;
import com.kopieczek.audinance.testutils.TestUtilities;

import java.util.ArrayList;

public abstract class AbstractConverterTest<T extends AbstractConverterTest> {
    final private String message;
    protected ArrayList<DecodedSource> inputChannels = new ArrayList<>();
    protected ArrayList<DecodedSource> expectedOutputChannels = new ArrayList<>();

    protected AbstractConverterTest(String message) {
       this.message = message;
    }

    public T withInputChannel(double... samples) {
        inputChannels.add(new MockDecodedSource(samples));
        return getThis();
    }

    public T expectingChannel(double... samples) {
        expectedOutputChannels.add(new MockDecodedSource(samples));
        return getThis();
    }

    public void run() {
        AudioFormat inputFormat = new AudioFormat(getInputSampleRate(), inputChannels.size());
        DecodedAudio inputAudio = new DecodedAudio(inputFormat, inputChannels.toArray(new DecodedSource[0]));

        AudioFormat outputFormat = new AudioFormat(getOutputSampleRate(), inputChannels.size());
        DecodedAudio expectedOutputAudio = new DecodedAudio(outputFormat, expectedOutputChannels.toArray(new DecodedSource[0]));

        DecodedAudio actualOutputAudio = doConversion(inputAudio);

        TestUtilities.assertDecodedAudioEqual(message, expectedOutputAudio, actualOutputAudio);
    }

    /**
     * Subclasses should override this with a method returning 'this'.
     * This is needed for use as a return value for the builder methods, as otherwise they would have to return
     * AbstractConverterTest, meaning subclasses' builder methods would not be accessible by chaining.
     */
    protected abstract T getThis();

    protected abstract Integer getInputSampleRate();
    protected abstract Integer getOutputSampleRate();
    protected abstract DecodedAudio doConversion(DecodedAudio input);
}
