package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class ScalableThreadPool {
    private final int minNumberOfThreads;
    private final int maxNumberOfThreads;
    private List<MyThread> myThreadPool = new ArrayList<>();
    private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private static Map<String, Boolean> threadsStates = new ConcurrentHashMap<>();


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
            queue.add(task);
            System.out.println("Adding new task to queue");
            queue.notify();
        }

    }

    private class MyThread extends Thread {
        @Override
        public void run() {
            Runnable task;
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (queue) {
                    //Если очередь пустая, то ожидаем
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.printf("%s is interrupting\n", Thread.currentThread().getName());
                            interrupt();
                        }
                    }
                }
                    //Если в очереди что-то появилось, то приступаем к работе
                    changeThreadsCount();
                    task = queue.poll();
                    task.run();

                    System.out.printf("%s completed the task!\n", Thread.currentThread().getName());

                synchronized (threadsStates) {
                    threadsStates.put(Thread.currentThread().getName(), false);
                }
                if(queue.isEmpty()) decreaseThreadsCount();
            }
            System.out.printf("Interrupting %s\n", Thread.currentThread().getName());
        }
    }
    private void changeThreadsCount(){
        synchronized (threadsStates) {
            threadsStates.put(Thread.currentThread().getName(), true);
            System.out.println(threadsStates.toString());
            if((threadsStates.size() == minNumberOfThreads) &&
                    threadsStates.values().stream().filter(Predicate.isEqual(false)).count() == 0){
                if(myThreadPool.size() == maxNumberOfThreads) return;
                for (int i = minNumberOfThreads; i < maxNumberOfThreads; i++) {
                    myThreadPool.add(new MyThread());
                    System.out.println("Increasing the number of threads");
                    myThreadPool.get(i).start();
                }
            }

        }
    }
    private void decreaseThreadsCount(){
        if(myThreadPool.size() == maxNumberOfThreads) {
            System.out.println(threadsStates.toString());
            System.out.println("Decreasing the number of threads");
            for (int i = minNumberOfThreads; i < maxNumberOfThreads; i++) {
                myThreadPool.get(i).interrupt();
                threadsStates.remove(Thread.currentThread().getName());

            }
        }
    }
}
