package com.kopieczek.audinance.conversion.multiplexers;

import com.kopieczek.audinance.audiosources.DecodedSource;
import com.kopieczek.audinance.formats.AudioFormat;
import com.kopieczek.audinance.formats.DecodedAudio;
import com.kopieczek.audinance.testutils.MockDecodedSource;
import com.kopieczek.audinance.testutils.TestUtilities;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.fail;

public class TestSimpleMultiplexer {
    @Test
    public void test_single_channel_plexed_to_one_channel_remains_unchanged() {
        new TestCase("Plexing from 1->1 channels should be a no-op")
                .withInputChannel(47.2, -7, 0.3, 9.8, Double.MAX_VALUE, Double.MIN_VALUE)
                .expectingChannel(47.2, -7, 0.3, 9.8, Double.MAX_VALUE, Double.MIN_VALUE)
                .run();
    }

    @Test
    public void test_three_channels_plexed_to_three_channels_should_remain_unchanged() {
        new TestCase("Plexing from 3->3 channels should be a no-op")
                .withInputChannel(4.4, 6.0, 0, 0.4, -3, 7)
                .withInputChannel(5.58, -9.99, -2, -7.3, 9.0, 5.9)
                .withInputChannel(-9, 3.2, 4.743, 5.8)
                .expectingChannel(4.4, 6.0, 0, 0.4, -3, 7)
                .expectingChannel(5.58, -9.99, -2, -7.3, 9.0, 5.9)
                .expectingChannel(-9, 3.2, 4.743, 5.8)
                .run();
    }

    @Test
    public void test_mono_to_stereo_duplicates_channel_data() {
        new TestCase("Plexing from 1->2 channels should just duplicate data")
                .withInputChannel(67.4, -15, 84.2)
                .expectingChannel(67.4, -15, 84.2)
                .expectingChannel(67.4, -15, 84.2)
                .run();
    }

    @Test
    public void test_mono_to_3_channels_triplicates_channel_data() {
        new TestCase("Plexing from 1->3 channels should just triplicate data")
                .withInputChannel(67.4, -15, 84.2)
                .expectingChannel(67.4, -15, 84.2)
                .expectingChannel(67.4, -15, 84.2)
                .expectingChannel(67.4, -15, 84.2)
                .run();
    }

    private class TestCase {
        final private String message;
        private ArrayList<double[]> inputChannelData = new ArrayList<>();
        private ArrayList<double[]> expectedOutputData = new ArrayList<>();

        TestCase(String message) {
            this.message = message;
        }

        TestCase withInputChannel(double... samples) {
            inputChannelData.add(samples);
            return this;
        }

        TestCase expectingChannel(double... samples) {
            expectedOutputData.add(samples);
            return this;
        }

        void run() {
            AudioFormat dummyFormat = new AudioFormat(null, inputChannelData.size());

            DecodedSource[] inputChannels = new DecodedSource[inputChannelData.size()];
            for (int idx = 0; idx < inputChannelData.size(); idx++) {
                inputChannels[idx] = new MockDecodedSource(inputChannelData.get(idx));
            }

            DecodedSource[] expectedOutputChannels = new DecodedSource[expectedOutputData.size()];
            for (int idx = 0; idx < expectedOutputData.size(); idx++) {
                expectedOutputChannels[idx] = new MockDecodedSource(expectedOutputData.get(idx));
            }

            DecodedAudio inputAudio = new DecodedAudio(dummyFormat, inputChannels);
            DecodedAudio expectedOutputAudio = new DecodedAudio(dummyFormat, expectedOutputChannels);

            Multiplexer multiplexer = new SimpleMultiplexer();
            DecodedAudio actualOutputAudio = multiplexer.toNChannels(inputAudio, expectedOutputChannels.length);

            TestUtilities.assertDecodedAudioEqual(message, expectedOutputAudio, actualOutputAudio);
        }
    }
}
