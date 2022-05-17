package org.example;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool {
    private final int numberOfThreads;
    private List<MyThread> myThreadPool = new ArrayList<>();
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public FixedThreadPool(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            myThreadPool.add(new MyThread());
            myThreadPool.get(i).start();
        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
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
                    task = queue.poll();
                    task.run();
                    System.out.printf("%s completed the task!\n", Thread.currentThread().getName());
                }
            }
            Thread.currentThread().interrupt();
        }
    }
}