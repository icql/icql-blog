---
title: java_concurrent_并发工具类
date: 2020-05-21 00:00:00
---

## LockSupport

java.util.concurrent.locks.LockSupport
底层使用的 Posix 协议的 mutex，condition 来实现的

``` java
//阻塞当前线程，直至调用unpark()
LockSupport.park();

//阻塞当前线程，加入纳秒超时
LockSupport.parkNanos(long nanos);

//阻塞当前线程，直至1970开始到deadline毫秒数
LockSupport.parkUntil(long deadline);

//唤醒阻塞的线程
LockSupport.unpark(Thread thread);
```

<br/>
<hr/>

## CountDownLatch

java.util.concurrent.CountDownLatch
用于多个子线程执行完成后主线程才开始继续执行，代替join的工具类
底层使用的是AQS实现的，利用state字段值来计数

``` java
CountDownLatch countDownLatch = new CountDownLatch(threadCount);

//每个线程里面执行完将计数器减一
run(){
    countDownLatch.countDown();
}

//阻塞当前线程直至计数器为0
countDownLatch.await();
```

<br/>
<hr/>

## CyclicBarrier

java.util.concurrent.CyclicBarrier
同步屏障，用于多个子线程内部到达一定点时再继续各自执行

``` java

CyclicBarrier c = new CyclicBarrier(2);
run(){
	c.await();
	//第一个线程需要到达同步点后执行的操作
}
run(){
	c.await();
	//第二个线程需要到达同步点后执行的操作
}
```

<br/>
<hr/>

## Semaphore

java.util.concurrent.Semaphore
信号量，控制并发线程数

``` java
Semaphore s = new Semaphore(10);
run(){
	s.acquire();
	//需要执行的操作
	s.release();
}
```

<br/>
<hr/>

## Exchanger

java.util.concurrent.Exchanger
交换两个线程的数据，相当于两个格子，当两个格子都被填满时交换

``` java
Exchanger<String> exchanger = new Exchanger<>();
run(){
	String re = exchanger.exchange("第一个线程");
	//re="第二个线程";
}
run(){
	String re = exchanger.exchange("第二个线程");
	//re="第一个线程";
}
```

<br/>
<hr/>


## Fork/Join框架
大计算量并发执行框架

<br/>
<hr/>

## CompletableFuture框架
使用 CompletableFuture + 线程池 并发执行任务
[使用示例](../../../resource/jc_并发工具类_CompletableFuture使用示例.java)