package com.task.challenge.services.impl.internal;

import java.util.concurrent.Future;

public class CounterTaskWithFutureReference {

    private final CounterTask task;
    private final Future<?> future;

    public CounterTaskWithFutureReference(CounterTask task, Future<?> future) {
        this.task = task;
        this.future = future;
    }

    public CounterTask getTask() {
        return task;
    }

    public Future<?> getFuture() {
        return future;
    }
}
