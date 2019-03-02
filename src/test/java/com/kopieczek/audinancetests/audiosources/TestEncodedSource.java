package com.kopieczek.audinancetests.audiosources;

import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import com.kopieczek.audinance.audiosources.EncodedSource;
import com.kopieczek.audinancetests.testutils.MockEncodedSource;

public class TestEncodedSource 
{
	public static int[] sTestData = new int[]
	{
		// Some tests are resilient to this changing, and some aren't.
		// If you play with these numbers, expect to have to fix things.
		8, 9, -2, 0, 2, 10, 129, 0
	};

	@Test
	public void testGetByte()
	{
		EncodedSource src = new MockEncodedSource(sTestData);
		for (int idx = 0; idx < sTestData.length; idx++)
		{
			Assert.assertEquals((byte)sTestData[idx], src.getByte(idx));
		}
	}
	
	@Test
	public void testGetBit()
	{	
		EncodedSource src = new MockEncodedSource(sTestData);		
		Assert.assertEquals(0, src.getBit(0));
		Assert.assertEquals(0, src.getBit(1));
		Assert.assertEquals(1, src.getBit(4));
		Assert.assertEquals(0, src.getBit(5));
		Assert.assertEquals(1, src.getBit(44));
		Assert.assertEquals(0, src.getBit(45));
		Assert.assertEquals(0, src.getBit(47));
	}
	
	@Test
	public void testIntFromBits()
	{
		EncodedSource src = new MockEncodedSource(sTestData);
		
		// Test a single byte.
		Assert.assertEquals(9, src.intFromBits(8, 8, ByteOrder.BIG_ENDIAN));
		
		// Test multiple whole bytes.
		Assert.assertEquals(2057, src.intFromBits(0, 16, ByteOrder.BIG_ENDIAN));
		
		// Test half a single byte.
		Assert.assertEquals(2, src.intFromBits(3, 3, ByteOrder.BIG_ENDIAN));
		
		// Test overlapping bytes.
		Assert.assertEquals(10, src.intFromBits(46, 4, ByteOrder.BIG_ENDIAN));
	}
}

