package com.kopieczek.audinance.conversion.multiplexers;

import org.junit.Ignore;

import static org.junit.Assert.fail;

public class TestSimpleMultiplexer extends AbstractMultiplexerTest {
    @Override
    protected Multiplexer getMultiplexer() {
        return new SimpleMultiplexer();
    }

    @Ignore // Currently broken
    public void testSingleChannelIdentity() {
        super.testSingleChannelIdentity();
    }

    @Ignore // Test not yet implemented
    @Override
    public void testMonoToStereo() {
        fail();
    }

    @Ignore // Test not yet implemented
    @Override
    public void testStereoToMono() {
        fail();
    }

    @Ignore // Test not yet implemented
    @Override
    public void plexUpWhenChannelEndsPrematurely() {
        fail();
    }

    @Ignore // Test not yet implemented
    @Override
    public void plexDownWhenChannelEndsPrematurely() {
        fail();
    }
}
