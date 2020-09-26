package com.cjsff.client;

import com.cjsff.transport.FrpcRequest;
import com.cjsff.transport.FrpcResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author cjsff
 */
public class FrpcFuture implements Future<Object> {

    private static final class Sync extends AbstractQueuedSynchronizer {
        /**
         * future status
         */
        private final int done = 1;
        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                return compareAndSetState(pending, done);
            } else {
                return true;
            }
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }

    private final Sync sync;
    private FrpcRequest request;
    private FrpcResponse response;

    private List<RpcCallback> pendingCallbacks = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public FrpcFuture(FrpcRequest request) {
        this.sync = new Sync();
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.response != null) {
            return this.response.getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            }
            return null;
        }
        throw new RuntimeException("Timeout exception.Request id: " + this.request.getId()
                + ". Request class name : " + this.request.getClassName()
                + ". Request method : " + this.request.getMethodName());
    }


    private void runCallback(final RpcCallback callback) {
        final FrpcResponse response = this.response;
        ClientWorkTask.submit(() -> {
            if (!response.isError()) {
                callback.success(response.getResult());
            } else {
                callback.fail(new RuntimeException("FrpcResponse error", new Throwable(response.getError())));
            }
        });
    }

    private void callback() {
        lock.lock();
        try {
            for (final RpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    public void done(FrpcResponse response) {
        this.response = response;
        sync.release(1);
        callback();
    }

}
