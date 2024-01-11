package org.jelly.app;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParameterTest 
{
    @Test
    public void replFileTest() {
        String[] s = {"repl", "file"};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.REPL_MODE);
        assertTrue(rn.hasFile());
        assertEquals(rn.getFilename(), "file");
    }

    @Test
    public void replNoFileTest() {
        String[] s = { "repl" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.REPL_MODE);
        assertFalse(rn.hasFile());
    }

    @Test
    public void batchFileTest() {
        String[] s = {"batch", "file"};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.BATCH_MODE);
        assertTrue(rn.hasFile());
        assertEquals(rn.getFilename(), "file");
    }

    @Test
    public void batchNoFileTest() {
        String[] s = { "batch" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.BATCH_MODE);
        assertFalse(rn.hasFile());
    }

    @Test
    public void defaultFileTest() {
        String[] s = { "file" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.REPL_MODE);
        assertTrue(rn.hasFile());
        assertEquals(rn.getFilename(), "file");
    }

    @Test
    public void defaultNoFileTest() {
        String[] s = {};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(rn.getRunningMode(), RunningMode.REPL_MODE);
        assertFalse(rn.hasFile());
    }
}
