package info634.alexnedelcu.com.info634.metrics;

import java.util.concurrent.locks.ReentrantLock;

import info634.alexnedelcu.com.info634.MainActivity;

/**
 * Created by Alex on 4/26/2016.
 */
public abstract class MetricBase extends MainActivity  {
    protected ReentrantLock lock = new ReentrantLock();
    protected static enum State {ACTIVE, INACTIVE};
    protected static State state;

    public MetricBase() {
        state = State.INACTIVE;
    }

    public abstract MetricDataObj getNewMetric();

    public abstract void clearData();

    public void pauseRecording() { state=State.INACTIVE; clearData(); }

    public void resumeRecording() { clearData(); state=State.ACTIVE; }


}
