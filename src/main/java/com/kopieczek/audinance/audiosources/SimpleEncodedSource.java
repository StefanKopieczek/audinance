package com.kopieczek.audinance.audiosources;

import com.kopieczek.audinance.utils.BitUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Naive implementation of EncodedAudioSource which simply reads all audio data into memory.
 */
public class SimpleEncodedSource extends EncodedSource
{
	private byte[] mData;
	
	public SimpleEncodedSource(InputStream is) throws IOException {
		mData = BitUtils.getByteArrayFromStream(is);
	}
	
	@Override
	public byte getByte(int index) throws NoMoreDataException
	{
	    if (index >= mData.length) {
	        throw new NoMoreDataException();
		}

        return mData[index];
	}

	@Override
	public int getLength() {
		return mData.length;
	}

	@Override
	public String toString() {
        return "<SimpleEncodedSource - " + mData.length + " bytes>";
    }
}
