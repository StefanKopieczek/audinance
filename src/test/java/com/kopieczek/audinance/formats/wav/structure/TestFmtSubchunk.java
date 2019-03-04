package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.formats.wav.WavEncodingType;
import com.kopieczek.audinance.testutils.MockEncodedSource;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.kopieczek.audinance.testutils.TestUtilities.encode;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFmtSubchunk {
    @Test
    public void test_simple_format_chunk_does_not_throw() {
        assertThatCode(() -> {
            FmtSubchunkBuilder
                    .withDefaultValues()
                    .build();
        }).doesNotThrowAnyException();
    }

    @Test
    public void test_get_endianism_inherited_from_riff_when_little_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.LITTLE_ENDIAN)
                .build();
        assertEquals(ByteOrder.LITTLE_ENDIAN, fmt.getEndianism());
    }

    @Test
    public void test_get_endianism_inherited_from_riff_when_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .build();
        assertEquals(ByteOrder.BIG_ENDIAN, fmt.getEndianism());
    }

    @Test
    public void test_chunk_size_offset_is_four() {
        FmtSubchunk fmt = FmtSubchunkBuilder.withDefaultValues().build();
        assertEquals(4, fmt.getChunkSizeIdxOffset());
    }

    @Test
    public void test_get_format_code_when_code_is_0x01() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x01)
                .build();
        assertEquals(0x01, fmt.getFormatCode());
    }

    @Test
    public void test_get_format_code_when_code_is_0x0101() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x0101)
                .build();
        assertEquals(0x101, fmt.getFormatCode());
    }

    @Ignore // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_format_code_when_code_is_0xffff() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0xffff)
                .build();
        assertEquals(0xffff, fmt.getFormatCode());
    }

    @Test
    public void test_get_format_code_when_code_is_0x01_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withFormatTag(0x01)
                .build();
        assertEquals(0x01, fmt.getFormatCode());
    }

    @Test
    public void test_get_format_code_when_code_is_0x0101_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withFormatTag(0x0101)
                .build();
        assertEquals(0x101, fmt.getFormatCode());
    }

    @Test
    public void test_get_format_code_when_code_is_0x01_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(7)
                .withFormatTag(0x01)
                .build();
        assertEquals(0x01, fmt.getFormatCode());
    }

    @Test
    public void test_get_format_code_when_code_is_0x0101_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(7)
                .withFormatTag(0x0101)
                .build();
        assertEquals(0x101, fmt.getFormatCode());
    }

    @Test
    public void test_format_code_1_is_pcm() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x0001)
                .build();
        assertEquals(WavEncodingType.PCM, fmt.getEncodingType());
    }

    @Test
    public void test_format_code_101_is_mulaw() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x0101)
                .build();
        assertEquals(WavEncodingType.MULAW, fmt.getEncodingType());
    }

    @Test
    public void test_format_code_102_is_alaw() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x0102)
                .build();
        assertEquals(WavEncodingType.ALAW, fmt.getEncodingType());
    }

    @Test
    public void test_format_code_103_is_adpcm() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x0103)
                .build();
        assertEquals(WavEncodingType.ADPCM, fmt.getEncodingType());
    }

    @Test
    public void test_get_num_channels_when_equal_to_1() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withNumChannels(1)
                .build();
        assertEquals(1, fmt.getNumChannels());
    }

    @Test
    public void test_get_num_channels_when_equal_to_2() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withNumChannels(2)
                .build();
        assertEquals(2, fmt.getNumChannels());
    }

    @Test
    public void test_get_num_channels_when_equal_to_17() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withNumChannels(17)
                .build();
        assertEquals(17, fmt.getNumChannels());
    }

    @Test
    public void test_get_num_channels_when_equal_to_17_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withNumChannels(17)
                .build();
        assertEquals(17, fmt.getNumChannels());
    }

    @Test
    public void test_get_num_channels_when_equal_to_17_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(100)
                .withNumChannels(17)
                .build();
        assertEquals(17, fmt.getNumChannels());
    }

    @Ignore  // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_num_channels_when_equal_to_0xffff() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withNumChannels(0xffff)
                .build();
        assertEquals(0xffff, fmt.getNumChannels());
    }

    @Test
    public void test_get_samples_per_sec_when_equal_to_1() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withSamplesPerSecond(1)
                .build();
        assertEquals(1, fmt.getSampleRate());
    }

    @Test
    public void test_get_samples_per_sec_when_equal_to_16000() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withSamplesPerSecond(16000)
                .build();
        assertEquals(16000, fmt.getSampleRate());
    }

    @Test
    public void test_get_samples_per_sec_when_equal_to_16000_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withSamplesPerSecond(16000)
                .build();
        assertEquals(16000, fmt.getSampleRate());
    }

    @Ignore  // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_samples_per_sec_when_equal_to_uint_max() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withSamplesPerSecond(4_294_967_295L)
                .build();
        assertEquals(4_294_967_295L, fmt.getSampleRate());
    }

    @Test
    public void test_get_samples_per_sec_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(18)
                .withSamplesPerSecond(44000)
                .build();
        assertEquals(44000, fmt.getSampleRate());
    }

    @Test
    public void test_get_bytes_per_sec_when_equal_to_1() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withAverageByteRate(1)
                .build();
        assertEquals(1, fmt.getByteRate());
    }

    @Test
    public void test_get_bytes_per_sec_when_equal_to_16000() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withAverageByteRate(16000)
                .build();
        assertEquals(16000, fmt.getByteRate());
    }

    @Test
    public void test_get_bytes_per_sec_when_equal_to_16000_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withAverageByteRate(16000)
                .build();
        assertEquals(16000, fmt.getByteRate());
    }

    @Ignore  // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_bytes_per_sec_when_equal_to_uint_max() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withAverageByteRate(4_294_967_295L)
                .build();
        assertEquals(4_294_967_295L, fmt.getByteRate());
    }

    @Test
    public void test_get_bytes_per_sec_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(18)
                .withAverageByteRate(44000)
                .build();
        assertEquals(44000, fmt.getByteRate());
    }

    @Test
    public void test_get_block_align_when_equal_to_1() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withBlockAlign(1)
                .build();
        assertEquals(1, fmt.getBlockAlign());
    }

    @Test
    public void test_get_block_align_when_equal_to_16() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withBlockAlign(16)
                .build();
        assertEquals(16, fmt.getBlockAlign());
    }

    @Test
    public void test_get_block_align_when_equal_to_16_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withBlockAlign(16)
                .build();
        assertEquals(16, fmt.getBlockAlign());
    }

    @Test
    public void test_get_block_align_when_equal_to_16_with_padding() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withPaddingSize(100)
                .withBlockAlign(16)
                .build();
        assertEquals(16, fmt.getBlockAlign());
    }

    @Ignore  // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_block_align_when_equal_to_0xffff() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withBlockAlign(0xffff)
                .build();
        assertEquals(0xffff, fmt.getBlockAlign());
    }

    @Test
    public void test_get_bit_depth_when_equal_to_8() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x01)
                .withPcmBitsPerSample(8)
                .build();
        assertEquals(8, fmt.getBitsPerSample());
    }

    @Test
    public void test_get_bit_depth_when_equal_to_16() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x01)
                .withPcmBitsPerSample(16)
                .build();
        assertEquals(16, fmt.getBitsPerSample());
    }

    @Test
    public void test_get_bit_depth_when_equal_to_32() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x01)
                .withPcmBitsPerSample(32)
                .build();
        assertEquals(32, fmt.getBitsPerSample());
    }

    @Test
    public void test_get_bit_depth_when_equal_to_32_big_endian() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withByteOrder(ByteOrder.BIG_ENDIAN)
                .withFormatTag(0x01)
                .withPcmBitsPerSample(32)
                .build();
        assertEquals(32, fmt.getBitsPerSample());
    }

    @Ignore  // https://github.com/StefanKopieczek/audinance/issues/1
    @Test
    public void test_get_bit_depth_when_equal_to_0xffff() {
        FmtSubchunk fmt = FmtSubchunkBuilder
                .withDefaultValues()
                .withFormatTag(0x01)
                .withPcmBitsPerSample(0xffff)
                .build();
        assertEquals(0xffff, fmt.getBitsPerSample());
    }

    private static class FmtSubchunkBuilder {
        private static final int TOTAL_UNPADDED_SIZE = 22;
        private static final int DATA_SIZE = TOTAL_UNPADDED_SIZE - 8; // 'fmt ' plus the chunk length
        private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        private int paddingSize;
        private int formatTag;
        private int numChannels;
        private long samplesPerSecond;
        private long averageBytesPerSecond;
        private int blockAlign;
        private Integer pcmBitsPerSample;

        static FmtSubchunkBuilder withDefaultValues() {
            return new FmtSubchunkBuilder()
                    .withPaddingSize(0)
                    .withByteOrder(ByteOrder.LITTLE_ENDIAN)
                    .withFormatTag(0x01)
                    .withNumChannels(1)
                    .withSamplesPerSecond(16000)
                    .withAverageByteRate(32000)
                    .withBlockAlign(16);
        }

        FmtSubchunkBuilder withByteOrder(ByteOrder order) {
            byteOrder = order;
            return this;
        }

        FmtSubchunkBuilder withPaddingSize(int size) {
            paddingSize = size;
            return this;
        }

        FmtSubchunkBuilder withFormatTag(int tag) {
            formatTag = tag;
            return this;
        }

        FmtSubchunkBuilder withNumChannels(int n) {
            numChannels = n;
            return this;
        }

        FmtSubchunkBuilder withSamplesPerSecond(long sampleRate) {
            samplesPerSecond = sampleRate;
            return this;
        }

        FmtSubchunkBuilder withAverageByteRate(long avgByteRate) {
            averageBytesPerSecond = avgByteRate;
            return this;
        }

        FmtSubchunkBuilder withBlockAlign(int blockAlign) {
            this.blockAlign = blockAlign;
            return this;
        }

        FmtSubchunkBuilder withPcmBitsPerSample(int bitsPerSample) {
            pcmBitsPerSample = bitsPerSample;
            return this;
        }

        FmtSubchunk build() {
            int extraDataBytes = (pcmBitsPerSample == null) ? 0 : 2;
            ByteBuffer bb = ByteBuffer.allocate(paddingSize + TOTAL_UNPADDED_SIZE + extraDataBytes)
                    .order(byteOrder)
                    .put(new byte[paddingSize])
                    .put(encode("fmt"))
                    .put((byte)0x00)
                    .putInt(DATA_SIZE)
                    .putShort((short)formatTag)
                    .putShort((short)numChannels)
                    .putInt((int)samplesPerSecond)
                    .putInt((int)averageBytesPerSecond)
                    .putShort((short)blockAlign);
            if (pcmBitsPerSample != null) {
                bb.putShort(pcmBitsPerSample.shortValue());
            }

            byte[] data = bb.array();

            RiffChunk mockRiff = mock(RiffChunk.class);
            when(mockRiff.getEndianism()).thenReturn(byteOrder);

            return new FmtSubchunk(new MockEncodedSource(data), paddingSize, mockRiff);
        }
    }
}
