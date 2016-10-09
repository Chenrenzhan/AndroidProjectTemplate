package com.drumge.template.common;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 自旋锁，在要求性能高和资源竞争不激烈的情况下使用。注意：这是不可重入锁
 * @author xianjiachao
 */
public class SpinLock {
    private AtomicReference<Thread> sign = new AtomicReference<>();

    public void lock() {
        Thread current = Thread.currentThread();
        while(!sign.compareAndSet(null, current)) {
        }
    }

    public void unlock() {
        Thread current = Thread.currentThread();
        sign.compareAndSet(current, null);
    }
}
