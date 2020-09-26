package com.cjsff.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author cjsff
 */
class ClientWorkTask {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("client-future-pool-%d").build();
    private static ExecutorService threadPoolExecutor =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() * 2,
                    Runtime.getRuntime().availableProcessors() * 2,
                    100L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),
                    namedThreadFactory,
                    new ThreadPoolExecutor.AbortPolicy());

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

}
