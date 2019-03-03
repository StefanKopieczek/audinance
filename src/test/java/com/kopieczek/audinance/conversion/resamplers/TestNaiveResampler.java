package com.kopieczek.audinance.conversion.resamplers;

import com.kopieczek.audinance.conversion.AbstractConverterTest;
import com.kopieczek.audinance.formats.DecodedAudio;
import org.junit.Test;

public class TestNaiveResampler {
	@Test
	public void test_resampling_to_same_rate_changes_nothing() {
	    new TestCase("Resampling to the same rate should not change anything")
				.withInputChannel(-1.0, 4.5, 17.2, 0, 3.9)
                    .withInputSampleRate(10000)
				.expectingChannel(-1.0, 4.5, 17.2, 0, 3.9)
                    .withOutputSampleRate(10000)
				.run();
	}

	@Test
	public void test_resampling_to_half_rate_drops_every_other_sample() {
	    new TestCase("Resampling to half the rate should drop alternate samples")
				.withInputChannel(-1.0, 4.5, 17.2, 0, 3.9)
					.withInputSampleRate(10000)
				.expectingChannel(-1.0, 17.2, 3.9)
					.withOutputSampleRate(5000)
				.run();
	}

	@Test
	public void test_resampling_to_double_rate_adds_new_samples_using_mean_of_neighbours() {
	    new TestCase("Doubling sample rate should interpolate using the average of neighbours")
				.withInputChannel(-1.0, 4.5, 17.2, 0, 3.9)
                    .withInputSampleRate(10000)
				.expectingChannel(-1.0, 1.75, 4.5, 10.85, 17.2, 8.6, 0, 1.95, 3.9)
                    .withOutputSampleRate(20000)
				.run();
	}

	private class TestCase extends AbstractConverterTest<TestCase> {
		private int inputSampleRate;
		private int outputSampleRate;

		TestCase(String message) {
		    super(message);
		}

		TestCase withInputSampleRate(int inputSampleRate) {
			this.inputSampleRate = inputSampleRate;
			return this;
		}

		TestCase withOutputSampleRate(int outputSampleRate) {
			this.outputSampleRate = outputSampleRate;
			return this;
		}

		@Override
		protected TestCase getThis() {
			return this;
		}

		@Override
		protected Integer getInputSampleRate() {
			return inputSampleRate;
		}

		@Override
		protected Integer getOutputSampleRate() {
			return outputSampleRate;
		}

		@Override
		protected DecodedAudio doConversion(DecodedAudio input) {
		    NaiveResampler resampler = new NaiveResampler();
		    return resampler.resample(input, outputSampleRate);
		}
	}
}
