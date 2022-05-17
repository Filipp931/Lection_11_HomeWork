package org.example;

import java.util.Random;

/**
 * Тестовый класс задания, вычисляющий факториал произвольного числа
 */
public class Task implements Runnable{
    private int num = new Random().nextInt(99999999);

    @Override
    public void run() {
        factorial(num);
    }
    private int factorial(int num){
        int result = 1;
        for (int i = 1; i <= num; i++) {
            result = result * i;
            String str = new String(String.valueOf(result));
        }
        return result;
    }
}
