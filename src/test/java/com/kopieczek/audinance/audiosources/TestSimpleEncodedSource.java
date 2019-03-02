package com.kopieczek.audinance.audiosources;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestSimpleEncodedSource {
    @Test
    public void test_first_byte_of_singleton_0() {
        SimpleEncodedSource src = buildSource(0x00);
        assertEquals(0x00, src.getByte(0));
    }

    @Test
    public void test_first_byte_of_singleton_7f() {
        SimpleEncodedSource src = buildSource(0x7f);
        assertEquals(0x7f, src.getByte(0));
    }

    @Test
    public void test_first_byte_of_singleton_neg_7f() {
        SimpleEncodedSource src = buildSource(-0x7f);
        assertEquals(-0x7f, src.getByte(0));
    }

    @Test
    public void test_first_byte_of_longer_source() {
        SimpleEncodedSource src = buildSource(0x01, 0x02, 0x03, 0x04);
        assertEquals(0x01, src.getByte(0));
    }

    @Test
    public void test_second_byte_of_longer_source() {
        SimpleEncodedSource src = buildSource(0x1a, 0x2b, 0x3c, 0x4d);
        assertEquals(0x2b, src.getByte(1));
    }

    @Test
    public void test_third_byte_of_three_byte_source() {
        SimpleEncodedSource src = buildSource(0x51, 0x62, 0x73);
        assertEquals(0x73, src.getByte(2));
    }

    @Test
    public void test_length_of_empty_source() {
        SimpleEncodedSource src = buildSource();
        assertEquals(0, src.getLength());
    }

    @Test
    public void test_length_of_singleton_source() {
        SimpleEncodedSource src = buildSource(0x7f);
        assertEquals(1, src.getLength());
    }

    @Test
    public void test_length_of_two_byte_source() {
        SimpleEncodedSource src = buildSource(0x01, 0x03);
        assertEquals(2, src.getLength());
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_first_byte_of_empty_source_throws_no_more_data_exception() {
        buildSource().getByte(0);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_second_byte_of_empty_source_throws_no_more_data_exception() {
        buildSource().getByte(1);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_third_byte_of_empty_source_throws_no_more_data_exception() {
        buildSource().getByte(2);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_second_byte_of_singleton_source_throws_no_more_data_exception() {
        buildSource(0x00).getByte(1);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_third_byte_of_singleton_source_throws_no_more_data_exception() {
        buildSource(0x00).getByte(3);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_byte_9_of_singleton_source_throws_no_more_data_exception() {
        buildSource(0x00).getByte(9);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_one_digit_too_far_in_long_source_throws_no_more_data_exception() {
        buildSource(-0x7f, 0x4f, 0x64, 0x23).getByte(4);
    }

    @Test(expected = NoMoreDataException.class)
    public void test_retrieving_much_too_far_in_long_source_throws_no_more_data_exception() {
        buildSource(-0x7f, 0x4f, 0x64, 0x23).getByte(400);
    }

    private static SimpleEncodedSource buildSource(int... contents) {
        byte[] bytes = new byte[contents.length];
        for (int idx = 0; idx < contents.length; idx++) {
            if (contents[idx] < -0x7f || contents[idx] > 0x7f) {
                throw new RuntimeException("Programmer error - byte " + contents[idx] + " at index " + idx + " out of range in test");
            }
            bytes[idx] = (byte)contents[idx];
        }

        try {
            return new SimpleEncodedSource(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
