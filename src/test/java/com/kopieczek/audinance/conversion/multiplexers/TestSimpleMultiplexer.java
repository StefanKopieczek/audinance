package com.kopieczek.audinance.conversion.multiplexers;

import com.kopieczek.audinance.conversion.AbstractConverterTest;
import com.kopieczek.audinance.formats.DecodedAudio;
import org.junit.Test;

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

    @Test
    public void test_stereo_to_mono_averages_data() {
        double[] channel1 = new double[] {10, 20, 30, 40, 50, -10, -20, -30, -40, -50};
        double[] channel2 = new double[] {1, -2, 3, -4, 5, -6, 7, -8, 9, -10};

        double[] expected = new double[channel1.length];
        for (int idx = 0; idx < channel1.length; idx++) {
            expected[idx] = (channel1[idx] + channel2[idx]) / 2;
        }

        new TestCase("Plexing from 2->1 channels should average both streams")
                .withInputChannel(channel1)
                .withInputChannel(channel2)
                .expectingChannel(expected)
                .run();
    }

    @Test
    public void test_stereo_to_mono_does_not_overflow() {
        new TestCase("Plexing from 2->1 channels should not overflow")
                .withInputChannel(Double.MAX_VALUE, Double.MIN_VALUE)
                .withInputChannel(Double.MAX_VALUE, Double.MIN_VALUE)
                .expectingChannel(Double.MAX_VALUE, Double.MIN_VALUE)
                .run();
    }

    @Test
    public void test_if_input_becomes_mono_during_plexing_then_we_pass_through_the_mono_data() {
        new TestCase("If input becomes mono during when plexing to 1 channel, we should just pass through the mono data")
                .withInputChannel(0, 2, 4, 4, 5, 6)
                .withInputChannel(0, 0, 0)
                .expectingChannel(0, 1, 2, 4, 5, 6)
                .run();
    }

    @Test
    public void test_plexing_two_to_three_channels_creates_new_channel_from_average_of_existing_two() {
        double[] channel1 = new double[] {17, -4, 10, 17.2, 100, 4};
        double[] channel2 = new double[] {-20, 13, 1, 2, -4, 4.4};

        double[] average = new double[channel1.length];
        for (int idx = 0; idx < channel1.length; idx++) {
            average[idx] = (channel1[idx] + channel2[idx]) / 2;
        }

        new TestCase("Plexing from 2->3 channels should preserve first two, and average them for the third channel")
                .withInputChannel(channel1)
                .withInputChannel(channel2)
                .expectingChannel(channel1)
                .expectingChannel(channel2)
                .expectingChannel(average)
                .run();
    }

    @Test
    public void test_plexing_two_to_six_channels_creates_four_new_channels_from_average_of_existing_two() {
        double[] channel1 = new double[] {17, -4, 10, 17.2, 100, 4};
        double[] channel2 = new double[] {-20, 13, 1, 2, -4, 4.4};

        double[] average = new double[channel1.length];
        for (int idx = 0; idx < channel1.length; idx++) {
            average[idx] = (channel1[idx] + channel2[idx]) / 2;
        }

        new TestCase("Plexing from 2->6 channels should preserve first two, and average them for the remaining channels")
                .withInputChannel(channel1)
                .withInputChannel(channel2)
                .expectingChannel(channel1)
                .expectingChannel(channel2)
                .expectingChannel(average)
                .expectingChannel(average)
                .expectingChannel(average)
                .expectingChannel(average)
                .run();
    }

    private class TestCase extends AbstractConverterTest<TestCase> {
        TestCase(String message) {
            super(message);
        }

        @Override
        protected TestCase getThis() {
            return this;
        }

        @Override
        protected Integer getInputSampleRate() {
            // Sample rate doesn't matter for multiplexing
            return null;
        }

        @Override
        protected Integer getOutputSampleRate() {
            // Sample rate doesn't matter for multiplexing
            return null;
        }

        @Override
        protected DecodedAudio doConversion(DecodedAudio input) {
            Multiplexer multiplexer = new SimpleMultiplexer();
            return multiplexer.toNChannels(input, expectedOutputChannels.size());
        }
    }
}
