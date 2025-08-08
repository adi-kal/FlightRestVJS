package com.ak.FB.helpers;

import java.util.concurrent.*;

public class ThreadManager {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static <T> Future<T> executeThread(Callable<T> task) {
        return executorService.submit(task);
    }

    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
