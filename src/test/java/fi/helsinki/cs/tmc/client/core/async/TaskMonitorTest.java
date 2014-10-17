package fi.helsinki.cs.tmc.client.core.async;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TaskMonitorTest {
    
    private TaskMonitor monitor;
    
    @Before
    public void setUp() {
        
        this.monitor = new TaskMonitor(3);
    }

    @Test
    public void canStart() {

        monitor.start();
        assertTrue(monitor.isStarted());
    }
    
    @Test
    public void initializesToZeroProgress() {
        
        assertEquals(0, monitor.progress());
    }
    
    @Test
    public void canIncrement() {
        
        monitor.increment();
        assertEquals(1, monitor.progress());
    }
    
    @Test
    public void canIncrementMultiple() {
        
        monitor.increment(2);
        assertEquals(2, monitor.progress());
    }
    
    @Test
    public void isFinishedAfterIncrementedToSteps() {
        
        monitor.start();
        monitor.increment(3);
        assertTrue(monitor.isFinished());
    }
    
    @Test
    public void isNotFinishedIfNotStartedEvenIfHasIncremented() {
        
        monitor.increment(3);
        assertFalse(monitor.isFinished());
    }
    
    @Test
    public void isNotFinishedIfNotIncrementedToStepAmount() {
        
        monitor.start();
        assertFalse(monitor.isFinished());
    }
}
