package com.mq.core.cache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mq.config.SystemConfig;

/**
 * semphore缓存，guava实现 操作key对应的Sephore对象
 * 
 * @author mengaijun
 * @Description: TODO
 * @date: 2019年9月24日 下午5:02:24
 */
public class SemaphoreCache {

    static org.slf4j.Logger LOG = LoggerFactory.getLogger(SemaphoreCache.class);

    private final static int hookTime = SystemConfig.SemaphoreCacheHookTimeValue;

    private static final LoadingCache<String, Semaphore> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(SystemConfig.AvailableProcessors).build(new CacheLoader<String, Semaphore>() {
                public Semaphore load(String input) throws Exception {
                    return new Semaphore(0);
                }
            });

    /**
     * 剩余可访问信号数量
     * 
     * @param key
     * @return int
     * @date: 2019年9月24日 下午5:03:12
     */
    public static int getAvailablePermits(String key) {
        try {
            return cache.get(key).availablePermits();
        } catch (ExecutionException ex) {
            LOG.error("缓存获取错误！", ex);
            return 0;
        }
    }

    /**
     * release 方法
     * 
     * @param key
     * @date: 2019年9月24日 下午5:06:18
     */
    public static void release(String key) {
        try {
            cache.get(key).release();
            TimeUnit.MILLISECONDS.sleep(hookTime);
        } catch (ExecutionException ex) {
            LOG.error("缓存获取错误！", ex);
        } catch (InterruptedException ex) {
            LOG.error("等待中断！", ex);
        }
    }

    /**
     * acquire方法
     * 
     * @param key
     * @date: 2019年9月24日 下午5:06:29
     */
    public static void acquire(String key) {
        try {
            cache.get(key).acquire();
            TimeUnit.MILLISECONDS.sleep(hookTime);
        } catch (InterruptedException ex) {
            LOG.error("缓存获取错误！", ex);
        } catch (ExecutionException ex) {
            LOG.error("等待中断！", ex);
        }
    }
}
