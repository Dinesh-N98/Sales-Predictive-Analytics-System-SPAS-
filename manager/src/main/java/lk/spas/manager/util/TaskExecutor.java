package lk.spas.manager.util;

import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class TaskExecutor {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, new DaemonThreadFactory());

    private TaskExecutor() {
    }

    public static void submit(Task<?> task) {
        EXECUTOR.submit(task);
    }

    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }

    private static class DaemonThreadFactory implements ThreadFactory {

        private int threadNumber;

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "manager-worker-" + ++threadNumber);
            thread.setDaemon(true);
            return thread;
        }
    }
}
