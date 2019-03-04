package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.testutils.MockEncodedSource;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.kopieczek.audinance.testutils.TestUtilities.encode;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDataSubchunk {
    @Test
    public void test_constructing_trivial_data_subchunk_does_not_throw() {
        assertThatCode(() ->
            new DataSubchunkBuilder()
                    .withBitsPerSample(8)
                    .withSamples()
                    .build()
        ).doesNotThrowAnyException();
    }

    @Test
    public void test_data_subchunk_is_little_endian_when_parent_riff_is() {
        DataSubchunk dataChunk = new DataSubchunkBuilder()
                .withByteOrder(ByteOrder.LITTLE_ENDIAN)
                .withBitsPerSample(8)
                .withSamples()
                .build();
        assertEquals(ByteOrder.LITTLE_ENDIAN, dataChunk.getEndianism());
    }

    @Test
    public void test_data_subchunk_is_big_endian_when_parent_riff_is() {
        DataSubchunk dataChunk = new DataSubchunkBuilder()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withBitsPerSample(8)
                .withSamples()
                .build();
        assertEquals(ByteOrder.BIG_ENDIAN, dataChunk.getEndianism());
    }

    @Test
    public void test_data_subchunk_chunk_size_offset_is_four() {
        DataSubchunk dataChunk = new DataSubchunkBuilder()
                .withBitsPerSample(8)
                .withSamples()
                .build();
        assertEquals(4, dataChunk.getChunkSizeOffset());
    }

    private static class DataSubchunkBuilder {
        private int paddingSize = 0;
        private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        private int bitsPerSample;
        private byte[] samples;

        DataSubchunkBuilder withPaddingSize(int paddingSize) {
            this.paddingSize = paddingSize;
            return this;
        }

        DataSubchunkBuilder withByteOrder(ByteOrder order) {
            byteOrder = order;
            return this;
        }

        DataSubchunkBuilder withBitsPerSample(int bitsPerSample) {
            this.bitsPerSample = bitsPerSample;
            return this;
        }

        DataSubchunkBuilder withSamples(int... sampleInts) {
            samples = new byte[sampleInts.length];
            for (int idx = 0; idx < sampleInts.length; idx++) {
                samples[idx] = (byte)sampleInts[idx];
            }
            return this;
        }

        DataSubchunk build() {
            byte[] data = ByteBuffer.allocate(paddingSize + 8 + samples.length)
                    .put(encode("data"))
                    .putInt(samples.length)
                    .put(samples)
                    .array();
            EncodedSource src = new MockEncodedSource(data);

            RiffChunk riff = mock(RiffChunk.class);
            when(riff.getEndianism()).thenReturn(byteOrder);

            return new DataSubchunk(src, paddingSize, riff, bitsPerSample);
        }
    }
}
