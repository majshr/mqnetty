package schedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.util.concurrent.Striped;

public class RingBufferWheel {
    private final static int DEFAULT_RING_SIZE = 64;
    private final static int DEFAULT_THREADS = 2;

    private Striped<Lock> striped = Striped.lazyWeakLock(5);

    private int ringSize;

    private List<Runnable>[] tasks;

    private int curIndex = 0;

    private ExecutorService executor;
    
    private int threads;
    
    private volatile boolean stop;

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public RingBufferWheel() {
        this(DEFAULT_RING_SIZE, DEFAULT_THREADS);
    }

    public RingBufferWheel(int size, int threads) {
        this.ringSize = size;
        this.threads = threads;
        tasks = new ArrayList[ringSize];
        executor = Executors.newFixedThreadPool(this.threads);
        start();
    }
    
    private void start() {
        new java.lang.Thread(new TriggerJob()).start();
    }

    /**
     * 相当于读信息，多个线程同时进行，有内部进行安全处理；但与remove（相当于写信息）是互斥的
     * 
     * @param task
     * @param delay
     *            void
     * @date: 2019年9月27日 下午2:35:36
     */
    public void addTask(Runnable task, int delay) {
        try {
            readWriteLock.readLock().lock();
            int cycleNumber = 0;
            int delayIndex = 0;
            // 计算任务环数；如果等待时间正好为圈数，那也是一圈
            if (delay < ringSize) {
                cycleNumber = 0;
            } else {
                cycleNumber = delay / ringSize;
            }
            // 计算任务所在下标
            delayIndex = (delay + curIndex) % ringSize;
            // 初始化位置数组
            if (tasks[delayIndex] == null) {
                initListAt(delayIndex);
            }
            // 添加任务
            synchronized (tasks[delayIndex]) {
                tasks[delayIndex].add(new DelayTaskWrapper(cycleNumber, task));
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    /**
     * 初始化数组所在下标的元素
     * 
     * @param index
     * @date: 2019年9月27日 上午11:18:43
     */
    private void initListAt(int index) {
        Lock lock = striped.get(index);

        try {
            lock.lock();
            if (tasks[index] == null) {
                tasks[index] = new ArrayList<>();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 移除下标需要执行的任务
     * 
     * @param index
     * @return List<Runnable>
     * @date: 2019年9月27日 上午11:39:37
     */
    private List<Runnable> remove(int index) {
        if (tasks[index] == null) {
            initListAt(index);
        }

        List<Runnable> runTasks = new ArrayList<>();
        Iterator<Runnable> iterator = tasks[index].iterator();
        while (iterator.hasNext()) {
            DelayTaskWrapper realTask = (DelayTaskWrapper) iterator.next();

            if (realTask.getCycleNumber() == 0) {
                runTasks.add(realTask);
                iterator.remove();
            } else {
                realTask.setCycleNumber(realTask.getCycleNumber() - 1);
            }

        }
        
        return runTasks;
    }

    private class TriggerJob implements Runnable {

        @Override
        public void run() {
            while (!stop) {
                // 启动任务
                // 执行当前下标任务的时候（remove操作），不允许向当前下标添加元素，
                List<Runnable> tasks = null;
                try {
                    System.out.println("获取任务：" + curIndex);
                    readWriteLock.writeLock().lock();
                    tasks = remove(curIndex);
                    curIndex++;
                    if (curIndex >= ringSize) {
                        curIndex = 0;
                    }
                } finally {
                    readWriteLock.writeLock().unlock();
                }

                tasks.forEach((task) -> {
                    executor.submit(task);
                });

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("等待中断！");
                    e.printStackTrace();
                }

            }
        }

    }

    /**
     * 延迟任务包装（包含了圈数信息）
     * 
     * @author mengaijun
     * @Description: TODO
     * @date: 2019年9月27日 上午11:30:12
     */
    public class DelayTaskWrapper implements Runnable {

        private int cycleNumber;

        private Runnable task;

        public DelayTaskWrapper(int cycleNumber, Runnable task) {
            this.cycleNumber = cycleNumber;
            this.task = task;
        }

        @Override
        public void run() {
            task.run();
        }

        public int getCycleNumber() {
            return cycleNumber;
        }

        public void setCycleNumber(int cycleNumber) {
            this.cycleNumber = cycleNumber;
        }

        public Runnable getTask() {
            return task;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }

    }
}
