package org.jelly.app;

import org.junit.jupiter.api.Test;

import static org.jelly.app.RunningParameters.RunningMode;
import static org.junit.jupiter.api.Assertions.*;

public class ParameterTest 
{
    @Test
    public void testReplArgv() {
        String[] s = {"repl", "things"};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.REPL_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
        assertArrayEquals(new String[]{"things"}, rn.getRuntimeArgv());
    }

    @Test
    public void testReplNoArgv() {
        String[] s = { "repl" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.REPL_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
        assertArrayEquals(new String[]{}, rn.getRuntimeArgv());
    }

    @Test
    public void testBatchArgv() {
        String[] s = {"batch", "things"};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.BATCH_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
        assertArrayEquals(new String[]{"things"}, rn.getRuntimeArgv());
    }

    @Test
    public void testNoBatchNoArgv() {
        String[] s = { "batch" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.BATCH_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
    }

    @Test
    public void testNoBatchNoRepl() {
        String[] s = { "mamma", "mia" };
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.REPL_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
        assertArrayEquals(new String[]{"mamma", "mia"}, rn.getRuntimeArgv());
    }

    @Test
    public void testEmptyArgv() {
        String[] s = {};
        RunningParameters rn = RunningParameters.fromArgs(s);
        assertEquals(RunningMode.REPL_MODE, rn.getRunningMode());
        assertFalse(rn.hasFile());
        assertArrayEquals(new String[]{}, rn.getRuntimeArgv());
    }
}
