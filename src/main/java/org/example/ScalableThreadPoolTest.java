package org.example;

/**
 * Hello world!
 *
 */
public class ScalableThreadPoolTest
{
    public static void main( String[] args )
    {

        System.out.println("===========ScalableThreadPool==========");
        ScalableThreadPool scalableThreadPool = new ScalableThreadPool(2,10);
        for (int i = 0; i < 20; i++) {
            Task task = new Task();
            scalableThreadPool.execute(task);
        }
    }

}
