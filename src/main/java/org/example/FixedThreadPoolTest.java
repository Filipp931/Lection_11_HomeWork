package org.example;

public class FixedThreadPoolTest {
    public static void main(String[] args) {
        System.out.println("=========FixedThreadPool==========");
        FixedThreadPool fixedThreadPool = new FixedThreadPool(10);
        for (int i = 0; i < 200; i++) {
            Task task = new Task();
            fixedThreadPool.execute(task);
        }
    }
}
