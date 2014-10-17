package fi.helsinki.cs.tmc.client.core.async.listener;

import fi.helsinki.cs.tmc.client.core.async.TaskResult;

import org.junit.Test;

public class AbstractTaskListenerTest {

    @Test
    public void canConstruct() {

        new AbstractTaskListener() {
            
            @Override
            public void onSuccess(final TaskResult<? extends Object> result) { }
            
            @Override
            public void onStart() { }
            
            @Override
            public void onInterrupt(final TaskResult<? extends Object> result) { }
            
            @Override
            public void onFailure(final TaskResult<? extends Object> result) { }
            
            @Override
            public void onEnd(final TaskResult<? extends Object> result) { }
        };
    }

}
