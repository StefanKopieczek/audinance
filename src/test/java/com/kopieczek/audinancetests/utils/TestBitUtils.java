package com.kopieczek.audinancetests.utils;

import org.junit.Assert;
import org.junit.Test;

import com.kopieczek.audinance.utils.BitUtils;

public class TestBitUtils 
{
	@Test
	public void testUintTo2sComplement()
	{
		Assert.assertEquals( 7,	 BitUtils.uintTo2sComplement(0b0111, 3));
		Assert.assertEquals(-1,  BitUtils.uintTo2sComplement(0b1111, 3));
		Assert.assertEquals( 0,	 BitUtils.uintTo2sComplement(0b00, 1));
		Assert.assertEquals(-2,	 BitUtils.uintTo2sComplement(0b10, 1));
		Assert.assertEquals( 26, BitUtils.uintTo2sComplement(0b0011010, 6));
		Assert.assertEquals(-38, BitUtils.uintTo2sComplement(0b1011010, 6));
	}
}
