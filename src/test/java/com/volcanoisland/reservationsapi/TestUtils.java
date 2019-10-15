package com.volcanoisland.reservationsapi;

import java.util.LinkedList;
import java.util.List;

public class TestUtils {

    /**
     * Runs the provided runnable in multiple parallel threads.
     * @param runnable
     * @param threadCount
     * @throws InterruptedException
     */
    public static void runMultithreaded(Runnable runnable, int threadCount) throws InterruptedException {
        List<Thread> threadList = new LinkedList<Thread>();
        for (int i = 0; i < threadCount; i++) {
            threadList.add(new Thread(runnable));
        }
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            t.join();
        }
    }

    /**
     * Runs the provided runnables in parallel, each on its own thread.
     * @param runnables
     * @throws InterruptedException
     */
    public static void runAllParallel(Runnable... runnables) throws InterruptedException {
        List<Thread> threadList = new LinkedList<Thread>();

        for (Runnable r : runnables) {
            threadList.add(new Thread(r));
        }
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            t.join();
        }
    }
}
