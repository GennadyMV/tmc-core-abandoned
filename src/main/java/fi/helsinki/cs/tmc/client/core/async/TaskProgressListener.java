package fi.helsinki.cs.tmc.client.core.async;


public interface TaskProgressListener {

    void onStart();

    void onProgress(int current, int estimatedTotal);

    void onEnd();
}
