package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

public class ScalableThreadPool {
    private final int minNumberOfThreads;
    private final int maxNumberOfThreads;
    private List<MyThread> myThreadPool = new ArrayList<>();
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private Map<String, Boolean> threadsStates = new ConcurrentHashMap<>();


    public ScalableThreadPool(int minNumberOfThreads, int maxNumberOfThreads) {
        this.minNumberOfThreads = minNumberOfThreads;
        this.maxNumberOfThreads = maxNumberOfThreads;
        for (int i = 0; i < minNumberOfThreads; i++) {
            myThreadPool.add(new MyThread());
            myThreadPool.get(i).start();
        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            synchronized (threadsStates){
                if(!threadsStates.isEmpty() &&
                        threadsStates.values().stream().filter(Predicate.isEqual(false)).count() == 0){
                    scaleThreadsCount();
                }
            }
            queue.add(task);
            System.out.println("Adding new task to queue");
            queue.notify();
        }

    }
    public void scaleThreadsCount(){
        for (int i = minNumberOfThreads; i < maxNumberOfThreads; i++) {
            myThreadPool.add(new MyThread());
            System.out.println("Increasing the number of threads");
            myThreadPool.get(i).start();
        }
    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            Runnable task;
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (threadsStates) {
                        threadsStates.put(Thread.currentThread().getName(), true);
                    }
                    task = queue.poll();
                    task.run();
                    try {
                        Thread.sleep(200000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("%s completed the task!\n", Thread.currentThread().getName());
                }
                synchronized (threadsStates) {
                    threadsStates.put(Thread.currentThread().getName(), false);
                }
            }
            Thread.currentThread().interrupt();
        }
    }
}
