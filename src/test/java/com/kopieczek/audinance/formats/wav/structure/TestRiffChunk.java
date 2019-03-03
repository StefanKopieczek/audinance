package com.kopieczek.audinance.formats.wav.structure;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinance.formats.wav.InvalidWavDataException;
import com.kopieczek.audinance.testutils.MockEncodedSource;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

public class TestRiffChunk {
    @Test
    public void test_riff_chunk_is_little_endian() {
        EncodedSource data = buildRiffData(8,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                              .put(encode("RIFF"))
                              .putInt(0));
        RiffChunk riff = new RiffChunk(data, 0);
        assertEquals(ByteOrder.LITTLE_ENDIAN, riff.getEndianism());
    }

    @Test
    public void test_rifx_chunk_is_big_endian() {
        EncodedSource data = buildRiffData(8,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                              .put(encode("RIFX"))
                              .putInt(0));
        RiffChunk riff = new RiffChunk(data, 0);
        assertEquals(ByteOrder.BIG_ENDIAN, riff.getEndianism());
    }

    @Test
    public void test_lowercase_riff_is_rejected() {
        EncodedSource data = buildRiffData(8,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("riff"))
                        .putInt(0));

        assertThatExceptionOfType(InvalidWavDataException.class)
                .isThrownBy(() -> new RiffChunk(data, 0).getEndianism());
    }

    @Test
    public void test_lowercase_rifx_is_rejected() {
        EncodedSource data = buildRiffData(8,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("rifx"))
                        .putInt(0));

        assertThatExceptionOfType(InvalidWavDataException.class)
                .isThrownBy(() -> new RiffChunk(data, 0).getEndianism());
    }

    @Test
    public void test_unknown_riff_header_is_rejected() {
        EncodedSource data = buildRiffData(8,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("HALP"))
                        .putInt(0));

        assertThatExceptionOfType(InvalidWavDataException.class)
                .isThrownBy(() -> new RiffChunk(data, 0).getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_riff_header_parsed_with_one_null_byte_pad() {
        EncodedSource data = buildRiffData(9,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte)0x00)
                        .put(encode("RIFF"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 1);
        assertEquals(ByteOrder.LITTLE_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_rifx_header_parsed_with_one_null_byte_pad() {
        EncodedSource data = buildRiffData(9,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put((byte)0x00)
                        .put(encode("RIFX"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 1);
        assertEquals(ByteOrder.BIG_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_riff_header_parsed_with_nine_null_byte_pad() {
        EncodedSource data = buildRiffData(17,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[9])
                        .put(encode("RIFF"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 9);
        assertEquals(ByteOrder.LITTLE_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_rifx_header_parsed_with_nine_null_byte_pad() {
        EncodedSource data = buildRiffData(17,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[9])
                        .put(encode("RIFX"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 9);
        assertEquals(ByteOrder.BIG_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_riff_header_parsed_with_single_non_null_byte_pad() {
        EncodedSource data = buildRiffData(9,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte)0x17)
                        .put(encode("RIFF"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 1);
        assertEquals(ByteOrder.LITTLE_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_rifx_header_parsed_with_single_non_null_byte_pad() {
        EncodedSource data = buildRiffData(9,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put((byte)0x17)
                        .put(encode("RIFX"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 1);
        assertEquals(ByteOrder.BIG_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_riff_header_parsed_with_longer_non_null_byte_pad() {
        EncodedSource data = buildRiffData(12,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[] {0x74, -0x13, 0x48, -0x66})
                        .put(encode("RIFF"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 4);
        assertEquals(ByteOrder.LITTLE_ENDIAN, riff.getEndianism());
    }

    @Ignore // Currently broken
    @Test
    public void test_rifx_header_parsed_with_longer_non_null_byte_pad() {
        EncodedSource data = buildRiffData(12,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[] {0x74, -0x13, 0x48, -0x66})
                        .put(encode("RIFX"))
                        .putInt(0));

        RiffChunk riff = new RiffChunk(data, 4);
        assertEquals(ByteOrder.BIG_ENDIAN, riff.getEndianism());
    }

    @Test
    public void test_id_index_offset_is_zero() {
        assertEquals(0, RiffChunk.ID_IDX_OFFSET);
    }

    @Test
    public void test_chunk_size_offset_is_four_bytes() {
        assertEquals(4, RiffChunk.CHUNK_SIZE_IDX_OFFSET);
        assertEquals(4, getDummyRiff().getChunkSizeIdxOffset());
    }

    @Test
    public void test_data_offset_is_twelve_bytes() {
        assertEquals(12, RiffChunk.DATA_IDX_OFFSET);
    }

    @Test
    public void test_get_start_index_when_zero() {
        EncodedSource riffData = getDummyRiffData();
        RiffChunk riff = new RiffChunk(riffData, 0);
        assertEquals(0, riff.getStartIndex());
    }

    @Test
    public void test_get_start_index_when_eleven() {
        EncodedSource riffData = getDummyRiffData();
        RiffChunk riff = new RiffChunk(riffData, 11);
        assertEquals(11, riff.getStartIndex());
    }

    @Test
    public void test_get_riff_length_when_data_section_is_empty() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                              .put(encode("RIFF"))
                              .putInt(0)
                              .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 0).getLength());
    }

    @Test
    public void test_get_riff_length_when_data_section_is_ten_bytes_long() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("RIFF"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 0).getLength());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_riff_length_when_data_section_is_empty_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFF"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 10).getLength());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_riff_length_when_data_section_is_ten_bytes_long_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFF"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 10).getLength());
    }

    @Test
    public void test_get_rifx_length_when_data_section_is_empty() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(encode("RIFX"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 0).getLength());
    }

    @Test
    public void test_get_rifx_length_when_data_section_is_ten_bytes_long() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(encode("RIFX"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 0).getLength());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_rifx_length_when_data_section_is_empty_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFX"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 10).getLength());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_rifx_length_when_data_section_is_ten_bytes_long_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFX"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 10).getLength());
    }

    @Test
    public void test_get_riff_end_index_when_data_section_is_empty() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("RIFF"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 0).getEndIndex());
    }

    @Test
    public void test_get_riff_end_index_when_data_section_is_ten_bytes_long() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("RIFF"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 0).getEndIndex());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_riff_end_index_when_data_section_is_empty_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFF"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 10).getEndIndex());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_riff_end_index_when_data_section_is_ten_bytes_long_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFF"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(28, new RiffChunk(riffData, 10).getLength());
    }

    @Test
    public void test_get_rifx_end_index_when_data_section_is_empty() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(encode("RIFX"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(8, new RiffChunk(riffData, 0).getEndIndex());
    }

    @Test
    public void test_get_rifx_end_index_when_data_section_is_ten_bytes_long() {
        EncodedSource riffData = buildRiffData(108,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(encode("RIFX"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 0).getEndIndex());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_rifx_end_index_when_data_section_is_empty_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFX"))
                        .putInt(0)
                        .put(new byte[100]));
        assertEquals(18, new RiffChunk(riffData, 8).getEndIndex());
    }

    @Ignore // Currently broken
    @Test
    public void test_get_rifx_end_index_when_data_section_is_ten_bytes_long_with_initial_offset() {
        EncodedSource riffData = buildRiffData(118,
                bytes -> bytes.order(ByteOrder.BIG_ENDIAN)
                        .put(new byte[10])
                        .put(encode("RIFX"))
                        .putInt(10)
                        .put(new byte[100]));
        assertEquals(28, new RiffChunk(riffData, 10).getEndIndex());
    }

    private static byte[] encode(String s) {
        return Charset.forName("ASCII").encode(s).array();
    }

    private static EncodedSource buildRiffData(int size, Consumer<ByteBuffer> bufferInitializer) {
        ByteBuffer bb = ByteBuffer.allocate(size);
        bufferInitializer.accept(bb);
        return new MockEncodedSource(bb.array());
    }

    private static EncodedSource getDummyRiffData() {
        return buildRiffData(8,
                bytes -> bytes.order(ByteOrder.LITTLE_ENDIAN)
                        .put(encode("RIFF"))
                        .putInt(0));
    }

    private static RiffChunk getDummyRiff() {
        return new RiffChunk(getDummyRiffData(), 0);
    }
}
