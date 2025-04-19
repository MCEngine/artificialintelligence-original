package io.github.mcengine.api.artificialintelligence;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Manages a thread pool executor for asynchronous task execution.
 * <p>
 * Configuration values for the thread pool are read from the plugin's {@code config.yml}:
 * <ul>
 *     <li>{@code threadpool.core} - number of core threads (default: 16)</li>
 *     <li>{@code threadpool.max} - maximum number of threads (default: 64)</li>
 *     <li>{@code threadpool.queue} - capacity of the task queue (default: 128)</li>
 *     <li>{@code threadpool.keepalive} - thread keep-alive time in seconds (default: 60)</li>
 * </ul>
 */
public class ThreadPoolManager {

    private final ThreadPoolExecutor executor;
    private final Logger logger;

    /**
     * Initializes the thread pool manager using configuration from the given plugin.
     *
     * @param plugin the Bukkit plugin instance, used to read configuration and log info
     */
    public ThreadPoolManager(Plugin plugin) {
        this.logger = plugin.getLogger();

        int coreThreads = plugin.getConfig().getInt("threadpool.core", 16);
        int maxThreads = plugin.getConfig().getInt("threadpool.max", 64);
        int queueCapacity = plugin.getConfig().getInt("threadpool.queue", 128);
        long keepAliveSeconds = plugin.getConfig().getLong("threadpool.keepalive", 60L);

        logger.info("[ThreadPoolManager] Initializing thread pool...");
        logger.info("[ThreadPoolManager] core=" + coreThreads + ", max=" + maxThreads + ", queue=" + queueCapacity);

        this.executor = new ThreadPoolExecutor(
                coreThreads,
                maxThreads,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Submits a task to the thread pool for asynchronous execution.
     *
     * @param task the {@link Runnable} task to execute
     */
    public void submit(Runnable task) {
        executor.submit(task);
    }

    /**
     * Initiates an orderly shutdown of the thread pool. No new tasks will be accepted.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks and halts the processing of waiting tasks.
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    /**
     * Returns the underlying {@link ExecutorService} for advanced control if needed.
     *
     * @return the thread pool executor instance
     */
    public ExecutorService getExecutor() {
        return executor;
    }
}
