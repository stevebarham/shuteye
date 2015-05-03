package net.ethx.shuteye.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreconditionsTest {
    @Test
    public void checkNotNullPass() {
        assertEquals("foo", Preconditions.checkNotNull("foo", "bar"));
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNull() {
        Preconditions.checkNotNull(null, "bar");
    }

    @Test
    public void checkArgumentPass() {
        Preconditions.checkArgument(1 == 1, "bar");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkArgument() {
        Preconditions.checkArgument(1 == 0, "bar");
    }

    @Test
    public void checkStatePass() {
        Preconditions.checkState(1 == 1, "bar");
    }

    @Test(expected = IllegalStateException.class)
    public void checkState() {
        Preconditions.checkState(1 == 0, "bar");
    }
}