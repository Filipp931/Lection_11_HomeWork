package org.example;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void FixedThreadPoolTest() {
        FixedThreadPool fixedThreadPool = new FixedThreadPool(10);
        for (int i = 0; i < 200; i++) {
            Task task = new Task();
            fixedThreadPool.execute(task);
        }
    }
    @Test
    public void FScalableThreadPoolTest() {
        ScalableThreadPool scalableThreadPool = new ScalableThreadPool(2,5);
        for (int i = 0; i < 100; i++) {
            Task task = new Task();
            scalableThreadPool.execute(task);
        }
    }
}
