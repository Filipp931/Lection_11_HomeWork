package org.example;

import java.util.*;

public class FixedThreadPool {
    private final List<Thread> myThreadPool = new ArrayList<>();
    private final Stack<Runnable> queue = new Stack<>();

    public FixedThreadPool(int numberOfThreads) {
        run(numberOfThreads);
    }

    private void run(int numberOfThreads) {
        for (int i = 0; i < numberOfThreads; i++) {
            myThreadPool.add(new Thread(new MyRunnable()));
            myThreadPool.get(i).start();
        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    private class MyRunnable implements Runnable {
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