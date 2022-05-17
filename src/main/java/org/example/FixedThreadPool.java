package org.example;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool {
    private final int numberOfThreads;
    private List<MyThread> myThreadPool = new ArrayList<>();
    private Stack<Runnable> queue = new Stack<>();

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
            while (!Thread.currentThread().isInterrupted()) {
                 synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
                    System.out.printf("%s Starting solve the task!\n", Thread.currentThread().getName());
                    queue.pop().run();
                    System.out.printf("%s completed the task!\n", Thread.currentThread().getName());
            }
            Thread.currentThread().interrupt();
        }
    }
}