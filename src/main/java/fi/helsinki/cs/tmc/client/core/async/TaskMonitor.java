package fi.helsinki.cs.tmc.client.core.async;


public class TaskMonitor {

    private final int steps;
    private boolean started;
    private int progress;

    public TaskMonitor(final int steps) {

        this.steps = steps;
    }

    public void start() {

        started = true;
    }

    public void increment() {

        progress++;
    }

    public void increment(final int amount) {

        progress += amount;
    }

    public boolean isStarted() {

        return started;
    }

    public boolean isFinished() {

        return started && progress >= steps;
    }

    public int progress() {

        return progress;
    }
}
