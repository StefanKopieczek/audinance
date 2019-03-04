package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.testutils.MockEncodedSource;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.kopieczek.audinance.testutils.TestUtilities.encode;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFmtSubchunk {
    @Test
    public void test_simple_format_chunk_does_not_throw() {
        assertThatCode(() -> {
            new FormatChunkBuilder()
                    .withPaddingSize(0)
                    .withByteOrder(ByteOrder.LITTLE_ENDIAN)
                    .withFormatTag(0x01)
                    .withNumChannels(1)
                    .withSamplesPerSecond(16000)
                    .withAverageByteRate(32000)
                    .withBlockAlign(16)
                    .build();
        }).doesNotThrowAnyException();
    }

    private static class FormatChunkBuilder {
        private static final int TOTAL_UNPADDED_SIZE = 22;
        private static final int DATA_SIZE = TOTAL_UNPADDED_SIZE - 8; // 'fmt ' plus the chunk length
        private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        private int paddingSize;
        private int formatTag;
        private int numChannels;
        private int samplesPerSecond;
        private int averageBytesPerSecond;
        private int blockAlign;

        FormatChunkBuilder withByteOrder(ByteOrder order) {
            byteOrder = order;
            return this;
        }

        FormatChunkBuilder withPaddingSize(int size) {
            paddingSize = size;
            return this;
        }

        FormatChunkBuilder withFormatTag(int tag) {
            formatTag = tag;
            return this;
        }

        FormatChunkBuilder withNumChannels(int n) {
            numChannels = n;
            return this;
        }

        FormatChunkBuilder withSamplesPerSecond(int sampleRate) {
            samplesPerSecond = sampleRate;
            return this;
        }

        FormatChunkBuilder withAverageByteRate(int avgByteRate) {
            averageBytesPerSecond = avgByteRate;
            return this;
        }

        FormatChunkBuilder withBlockAlign(int blockAlign) {
            this.blockAlign = blockAlign;
            return this;
        }

        FmtSubchunk build() {
            byte[] data = ByteBuffer.allocate(paddingSize + TOTAL_UNPADDED_SIZE)
                    .put(encode("fmt"))
                    .putInt(DATA_SIZE)
                    .putShort((short)formatTag)
                    .putShort((short)numChannels)
                    .putInt(samplesPerSecond)
                    .putInt(averageBytesPerSecond)
                    .putShort((short)blockAlign)
                    .array();

            RiffChunk mockRiff = mock(RiffChunk.class);
            when(mockRiff.getEndianism()).thenReturn(byteOrder);

            return new FmtSubchunk(new MockEncodedSource(data), paddingSize, mockRiff);
        }
    }
}
