package org.example;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

public class ScalableThreadPool {
    private final int minNumberOfThreads;
    private final int maxNumberOfThreads;
    private final List<Thread> myThreadPool = new ArrayList<>();
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private static final Map<String, Boolean> threadsStates = new ConcurrentHashMap<>();


    public ScalableThreadPool(int minNumberOfThreads, int maxNumberOfThreads) {
        this.minNumberOfThreads = minNumberOfThreads;
        this.maxNumberOfThreads = maxNumberOfThreads;
        start();
    }

    private void start() {
        for (int i = 0; i < minNumberOfThreads; i++) {
            myThreadPool.add(new Thread(new MyThread()));
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

    private class MyThread implements Runnable {
        @Override
        public void run() {
            Runnable task;
            while (!Thread.currentThread().isInterrupted()) {
                //Если очередь пустая, то ожидаем
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.printf("%s is interrupting\n", Thread.currentThread().getName());
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                //Если в очереди что-то появилось, то приступаем к работе
                task = queue.poll();                                         // получаем задание из очереди
                threadsStates.put(Thread.currentThread().getName(), true);   // устанавливаем его состояние как "занят"
                System.out.println(threadsStates.toString());
                changeThreadsCount();                                        // увеличиваем число потоков (при необходимости)
                task.run();                                                  // выполняем задание
                System.out.printf("%s completed the task!\n", Thread.currentThread().getName());
                threadsStates.put(Thread.currentThread().getName(), false);
                if(queue.isEmpty()) decreaseThreadsCount();                  // уменьшаем число потоков (при необходимости)
            }
            System.out.printf("Interrupting %s\n", Thread.currentThread().getName());
        }
    }

    /**
     * Увеличение числа потоков, если все текущие потоки заняты
     */
    private void changeThreadsCount(){
            if((threadsStates.size() == minNumberOfThreads) &&                              //если количество потоков минимально
                    threadsStates.values().stream().noneMatch(Predicate.isEqual(false)) &&  //если нет свободных
                    !queue.isEmpty()){                                                      //если очередь еще не пуста
                for (int i = minNumberOfThreads; i < maxNumberOfThreads; i++) {
                    myThreadPool.add(new Thread(new MyThread()));
                    System.out.println("Increasing the number of threads");
                    myThreadPool.get(i).start();
                }
            }
    }

    /**
     * Прерывание потоков
     */
    private void decreaseThreadsCount(){
        if(myThreadPool.size() == maxNumberOfThreads) {                                     //если число потоков максимально, то прерываем лишние
            System.out.println("Decreasing the number of threads");
            for (int i = minNumberOfThreads; i < maxNumberOfThreads; i++) {
                myThreadPool.get(i).interrupt();
            }
        }
    }
}
