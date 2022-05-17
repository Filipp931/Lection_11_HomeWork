package org.example;

import java.util.Random;

/**
 * Тестовый класс задания, вычисляющий факториал произвольного числа
 */
public class Task implements Runnable{
    private int num = new Random().nextInt(5000);
    @Override
    public void run() {
        factorial(num);
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.printf("Factorial of %d is calculated\n", num);
    }
    private int factorial(int num){
     if(num == 1) return 1;
     if(num == 0 ) return 0;
     else return factorial(num -1);
    }
}
