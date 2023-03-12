---
title: java_concurrent_线程池
date: 2020-06-15 00:00:00
---

## 异步任务

### 1）Runnable、Callable、Future
``` java
//无返回值的任务接口
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}

//有返回值的任务接口
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}

//任务管理接口
public interface Future<V> {
    //取消任务
    boolean cancel(boolean mayInterruptIfRunning);
    
    //任务是否已经取消，线程运行结束返回true
    boolean isCancelled();
    
    //任务是否执行完成，线程运行结束返回true
    boolean isDone();

    //阻塞等待结果返回
    V get() throws InterruptedException, ExecutionException;

    //阻塞等待结果返回，有超时
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

### 2）FutureTask（实现上述 3 个接口的具体类）

#### （1）继承关系
``` java
//FutureTask 继承 Runnable，Future，组合Callable
public interface RunnableFuture<V> extends Runnable, Future<V> {
    void run();
}

public class FutureTask<V> implements RunnableFuture<V> {
    private Callable<V> callable;
}
```

#### （2）字段属性

``` java
//任务状态
private volatile int state;
private static final int NEW          = 0;//任务创建
private static final int COMPLETING   = 1;//任务执行中
private static final int NORMAL       = 2;//任务结束
private static final int EXCEPTIONAL  = 3;//任务异常
private static final int CANCELLED    = 4;//任务取消成功
private static final int INTERRUPTING = 5;//任务正在被打断中
private static final int INTERRUPTED  = 6;//任务被打断成功

//组合了 Callable
private Callable<V> callable;

//任务返回的接口
private Object outcome;

//当前任务所运行的线程
private volatile Thread runner;

//记录调用 get 方法时被等待的线程
private volatile WaitNode waiters;

//unsafe操作相关字段
private static final sun.misc.Unsafe UNSAFE;
private static final long stateOffset;
private static final long runnerOffset;
private static final long waitersOffset;
```

#### （3）构造方法

``` java
public FutureTask(Callable<V> callable) {
	if (callable == null)
		throw new NullPointerException();
	this.callable = callable;
	this.state = NEW;
}

public FutureTask(Runnable runnable, V result) {
	//将 runnable 适配为 callable
	this.callable = Executors.callable(runnable, result);
	this.state = NEW;
}

//适配器模式，将 runnable 适配为 callable
public class Executors {
    static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }
}
```

#### （4）关键方法

##### （4.1）isCancelled方法
``` java
//任务是否已经取消
public boolean isCancelled() {
	return state >= CANCELLED;
}
```
##### （4.2）isDone方法
``` java
//任务是否已经执行完成
public boolean isDone() {
	return state != NEW;
}
```

##### （4.3）cancel方法
``` java
//取消任务
public boolean cancel(boolean mayInterruptIfRunning) {
	//任务状态是创建 且 new 状态置为取消/被打断
	//失败，返回 false
	//成功，继续执行
	if (!(state == NEW &&
		  UNSAFE.compareAndSwapInt(this, stateOffset, NEW,
			  mayInterruptIfRunning ? INTERRUPTING : CANCELLED)))
		return false;
	try {
		//如果入参传递打断，则执行
		if (mayInterruptIfRunning) {
			try {
				Thread t = runner;
				if (t != null)
					t.interrupt();
			} finally { //final state
				UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
			}
		}
	} finally {
		//唤醒等待获取此任务结果的线程
		finishCompletion();
	}
	return true;
}

//唤醒等待获取此任务结果的线程
private void finishCompletion() {
	//遍历等待的线程唤醒，单链表
	for (WaitNode q; (q = waiters) != null;) {
		if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
			for (;;) {
				Thread t = q.thread;
				if (t != null) {
					q.thread = null;
					//唤醒
					LockSupport.unpark(t);
				}
				WaitNode next = q.next;
				if (next == null)
					break;
				q.next = null; //unlink to help gc
				q = next;
			}
			break;
		}
	}
	
	//扩展点，此类未实现，待子类重写
	done();

	callable = null;        //to reduce footprint
}
```
##### （4.4）get方法
``` java
//阻塞等待结果返回
public V get() throws InterruptedException, ExecutionException {
	int s = state;
	if (s <= COMPLETING)
		//阻塞等待具体逻辑
		s = awaitDone(false, 0L);
	//任务已经执行完成，返回结果
	return report(s);
}

//阻塞等待结果返回，有超时
public V get(long timeout, TimeUnit unit)
	throws InterruptedException, ExecutionException, TimeoutException {
	if (unit == null)
		throw new NullPointerException();
	int s = state;
	if (s <= COMPLETING &&
		//阻塞等待具体逻辑
		(s = awaitDone(true, unit.toNanos(timeout))) <= COMPLETING)
		throw new TimeoutException();
	//返回结果	
	return report(s);
}

//阻塞等待结果返回，具体逻辑
private int awaitDone(boolean timed, long nanos)
	throws InterruptedException {
	final long deadline = timed ? System.nanoTime() + nanos : 0L;
	WaitNode q = null;
	boolean queued = false;
	//自旋等待
	for (;;) {
		if (Thread.interrupted()) {
			removeWaiter(q);
			throw new InterruptedException();
		}

		int s = state;
		//任务执行完成，返回
		if (s > COMPLETING) {
			if (q != null)
				q.thread = null;
			return s;
		}
		//如果任务正在执行，让出cpu调度
		else if (s == COMPLETING) //cannot time out yet
			Thread.yield();
		//如果第一次运行，设置等待获取结果的线程waitNode，即当前调用的线程
		else if (q == null)
			q = new WaitNode();
		else if (!queued)
			//这里是使用头插法插入等待线程到单链表
			queued = UNSAFE.compareAndSwapObject(this, waitersOffset,
												 q.next = waiters, q);
		else if (timed) {
			nanos = deadline - System.nanoTime();
			if (nanos <= 0L) {
				removeWaiter(q);
				return state;
			}
			//没有过超时时间，等待唤醒
			LockSupport.parkNanos(this, nanos);
		}
		else
			//没有设置超时时间，等待唤醒
			LockSupport.park(this);
	}
}

//返回结果
@SuppressWarnings("unchecked")
private V report(int s) throws ExecutionException {
	Object x = outcome;
	if (s == NORMAL)
		return (V)x;
	if (s >= CANCELLED)
		throw new CancellationException();
	throw new ExecutionException((Throwable)x);
}
```

##### （4.5）run方法
``` java
//没有返回值，通过给outcome设置返回值
public void run() {
	if (state != NEW ||
		!UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
		return;
	try {
		Callable<V> c = callable;
		if (c != null && state == NEW) {
			V result;
			boolean ran;
			try {
				result = c.call();
				ran = true;
			} catch (Throwable ex) {
				result = null;
				ran = false;
				setException(ex);
			}
			if (ran)
				set(result);
		}
	} finally {
		//runner must be non-null until state is settled to
		//prevent concurrent calls to run()
		runner = null;
		//state must be re-read after nulling runner to prevent
		//leaked interrupts
		int s = state;
		if (s >= INTERRUPTING)
			handlePossibleCancellationInterrupt(s);
	}
}

//outcome设置返回值
protected void set(V v) {
	if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
		outcome = v;
		UNSAFE.putOrderedInt(this, stateOffset, NORMAL); //final state
		finishCompletion();
	}
}
```


<br/>
<hr/>

## ThreadPoolExecutor（线程池）

1、降低资源消耗
2、提高响应速度
3、提高线程的复用性

![总体架构图](../../../resource/jc_线程池_总体架构图.jfif)

### 1）类结构

ThreadPoolExecutor -> AbstractExecutorService -> ExecutorService -> Executor

``` java
//继承关系
public interface Executor {
    void execute(Runnable command);
}

public interface ExecutorService extends Executor {}

public abstract class AbstractExecutorService implements ExecutorService {}

public class ThreadPoolExecutor extends AbstractExecutorService {}
```

### 2）字段属性

#### （1）线程池状态

![线程池状态图](../../../resource/jc_线程池_线程池状态图.jfif)

``` java
//ctl 线程池状态控制字段，由两部分组成：
//1:workerCount  wc 工作线程数，我们限制 workerCount 最大到(2^29)-1，大概 5 亿个线程
//2:runState rs 线程池的状态，提供了生命周期的控制，源码中有很多关于状态的校验，状态枚举如下：
//RUNNING（-536870912）：接受新任务或者处理队列里的任务。
//SHUTDOWN（0）：不接受新任务，但仍在处理已经在队列里面的任务。
//STOP（536870912）：不接受新任务，也不处理队列中的任务，对正在执行的任务进行中断。
//TIDYING（1073741824）： 所以任务都被中断，workerCount 是 0，整理状态
//TERMINATED（1610612736）： terminated() 已经完成的时候

//runState 之间的转变过程：
//RUNNING -> SHUTDOWN：调用 shudown(),finalize()
//(RUNNING or SHUTDOWN) -> STOP：调用shutdownNow()
//SHUTDOWN -> TIDYING -> workerCount ==0
//STOP -> TIDYING -> workerCount ==0
//TIDYING -> TERMINATED -> terminated() 执行完成之后
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
private static final int COUNT_BITS = Integer.SIZE - 3;//29
private static final int CAPACITY   = (1 << COUNT_BITS) - 1;//=(2^29)-1=536870911

//Packing and unpacking ctl
private static int ctlOf(int rs, int wc) { return rs | wc; }
private static int workerCountOf(int c)  { return c & CAPACITY; }
private static int runStateOf(int c)     { return c & ~CAPACITY; }

//runState is stored in the high-order bits
private static final int RUNNING    = -1 << COUNT_BITS;//-536870912
private static final int SHUTDOWN   =  0 << COUNT_BITS;//0
private static final int STOP       =  1 << COUNT_BITS;//-536870912
private static final int TIDYING    =  2 << COUNT_BITS;//1073741824
private static final int TERMINATED =  3 << COUNT_BITS;//1610612736

//已完成任务的计数
volatile long completedTasks;
//线程池最大容量
private int largestPoolSize;
//已经完成的任务数
private long completedTaskCount;
//用户可控制的参数都是 volatile 修饰的
//可以使用 threadFactory 创建 thread
//创建失败一般不抛出异常，只有在 OutOfMemoryError 时候才会
private volatile ThreadFactory threadFactory;
//饱和或者运行中拒绝任务的 handler 处理类
private volatile RejectedExecutionHandler handler;
//线程存活时间设置
private volatile long keepAliveTime;
//设置 true 的话，核心线程空闲 keepAliveTime 时间后，也会被回收
private volatile boolean allowCoreThreadTimeOut;
//coreSize
private volatile int corePoolSize;
//maxSize 最大限制 (2^29)-1
private volatile int maximumPoolSize;
//默认的拒绝策略
private static final RejectedExecutionHandler defaultHandler =
    new AbortPolicy();

//队列会 hold 住任务，并且利用队列的阻塞的特性，来保持线程的存活周期
private final BlockingQueue<Runnable> workQueue;

//大多数情况下是控制对 workers 的访问权限
private final ReentrantLock mainLock = new ReentrantLock();
private final Condition termination = mainLock.newCondition();

//包含线程池中所有的工作线程
private final HashSet<Worker> workers = new HashSet<Worker>();
```

#### （2）worker（工作单元）
worker 是线程池中任务运行的最小单元

``` java
//线程池中任务执行的最小单元
//Worker 继承 AQS，具有锁功能
//Worker 实现 Runnable，本身是一个可执行的任务
private final class Worker
    extends AbstractQueuedSynchronizer
    implements Runnable{

    //任务运行的线程
    final Thread thread;

    //需要执行的任务
    Runnable firstTask;

    //Worker本身是个 Runnable,把自己作为任务传递给 thread
    //Thread 和 Worker 相互持有
    Worker(Runnable firstTask) {
        setState(-1); //inhibit interrupts until runWorker
        this.firstTask = firstTask;
        //把 Worker 自己作为 thread 运行的任务
        this.thread = getThreadFactory().newThread(this);
    }

   /** Worker 本身是 Runnable，run 方法是 Worker 执行的入口， runWorker 是外部的方法 */
    public void run() {
        runWorker(this);
    }

    private static final long serialVersionUID = 6138294804551838833L;

    //Lock methods
    //0 代表没有锁住，1 代表锁住
    protected boolean isHeldExclusively() {
        return getState() != 0;
    }
    //尝试加锁，CAS 赋值为 1，表示锁住
    protected boolean tryAcquire(int unused) {
        if (compareAndSetState(0, 1)) {
            setExclusiveOwnerThread(Thread.currentThread());
            return true;
        }
        return false;
    }
    //尝试释放锁，释放锁没有 CAS 校验，可以任意的释放锁
    protected boolean tryRelease(int unused) {
        setExclusiveOwnerThread(null);
        setState(0);
        return true;
    }

    public void lock()        { acquire(1); }
    public boolean tryLock()  { return tryAcquire(1); }
    public void unlock()      { release(1); }
    public boolean isLocked() { return isHeldExclusively(); }

    void interruptIfStarted() {
        Thread t;
        if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
            try {
                t.interrupt();
            } catch (SecurityException ignore) {
            }
        }
    }
}
```


### 3）任务执行流程

线程池任务执行流程
![线程池任务执行流程](../../../resource/jc_线程池_线程池任务执行流程图.jfif)

#### （1）submit方法

提交任务 submit 是 AbstractExecutorService 实现的

``` java
//1）把 Runnable 和 Callable 都转化成 FutureTask，newTaskFor方法调用FutureTask构造方法（上面的内容）
//2）使用 java.util.concurrent.Executor#execute 方法执行 FutureTask
//java.util.concurrent.ThreadPoolExecutor#execute 实现的

public Future<?> submit(Runnable task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<Void> ftask = newTaskFor(task, null);
    execute(ftask);
    return ftask;
}

public <T> Future<T> submit(Runnable task, T result) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<T> ftask = newTaskFor(task, result);
    execute(ftask);
    return ftask;
}

public <T> Future<T> submit(Callable<T> task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<T> ftask = newTaskFor(task);
    execute(ftask);
    return ftask;
}
```

#### （2）execute方法
ThreadPoolExecutor 实现 Executor的接口的方法

``` java
//execute方法中，根据入参不同，调用addWorker方法
//addWorker(firstTask,true) 创建核心线程，执行提交任务
//addWorker(firstTask,false) 创建非核心线程，执行提交任务
//addWorker(null,true) 创建核心线程，执行工作队列中任务
//addWorker(null,false) 创建非核心线程，执行工作队列中任务

public void execute(Runnable command) {
    if (command == null)
        throw new NullPointerException();
    int c = ctl.get();
    //工作的线程小于核心线程数，创建新的线程，成功返回，失败不抛异常
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return;
        //线程池状态可能发生变化
        c = ctl.get();
    }
    //工作的线程大于等于核心线程数，或者新建线程失败
    //线程池状态正常，并且可以入队的话，尝试入队列
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        //如果线程池状态异常 尝试从队列中移除任务，可以移除的话就拒绝掉任务
        if (!isRunning(recheck) && remove(command))
            reject(command);
        //发现可运行的线程数是 0，就初始化一个线程
        //这里是个极限情况，入队的时候，突然发现可用线程都被回收了
        else if (workerCountOf(recheck) == 0)
            //Runnable是空的，不会影响新增线程，但是线程在 start 的时候不会运行
            //Thread.run() 里面有判断
            addWorker(null, false);
    }
    //队列满了，开启线程到 maxSize，如果失败直接拒绝,
    else if (!addWorker(command, false))
        reject(command);
}
```

#### （3）addWorker方法

结合线程池的情况看是否可以添加新的 worker，ThreadPoolExecutor.addWorker 方法

``` java
//最终执行任务是调用 Worker.start方法，实际上是执行 Worker.run方法

//firstTask 不为空可以直接执行，为空执行不了，Thread.run()方法有判断，Runnable为空不执行
//core 为 true 表示线程最大新增个数是 coresize，false 表示最大新增个数是 maxsize
//返回 true 代表成功，false 失败
//break retry 跳到retry处，且不再进入循环
//continue retry 跳到retry处，且再次进入循环
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    //先是各种状态的校验
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
        //Check if queue empty only if necessary.
        //rs >= SHUTDOWN 说明线程池状态不正常
        if (rs >= SHUTDOWN &&
            ! (rs == SHUTDOWN &&
               firstTask == null &&
               ! workQueue.isEmpty()))
            return false;

        for (;;) {
            int wc = workerCountOf(c);
            //工作中的线程数大于等于容量，或者大于等于 coreSize or maxSize
            if (wc >= CAPACITY ||
                wc >= (core ? corePoolSize : maximumPoolSize))
                return false;
            if (compareAndIncrementWorkerCount(c))
                //break 结束 retry 的 for 循环
                break retry;
            c = ctl.get();  //Re-read ctl
            //线程池状态被更改
            if (runStateOf(c) != rs)
                //跳转到retry位置
                continue retry;
            //else CAS failed due to workerCount change; retry inner loop
        }
    }

    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        //Worker 本身是个 Runnable.
        //在初始化的过程中，会把 worker 丢给 thread 去初始化
        w = new Worker(firstTask);
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                //Recheck while holding lock.
                //Back out on ThreadFactory failure or if
                //shut down before lock acquired.
                int rs = runStateOf(ctl.get());
                if (rs < SHUTDOWN ||
                    (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive()) //precheck that t is startable
                        throw new IllegalThreadStateException();
                    workers.add(w);
                    int s = workers.size();
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                //启动线程，实际上去执行 Worker.run 方法
                t.start();
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)
            addWorkerFailed(w);
    }
    return workerStarted;
}
```

#### （4）Worker.run方法

执行任务 ThreadPoolExecutor.Worker.run 方法

``` java
//核心线程未创建足够时，都是直接创建后进入runWorker执行
//执行完成后，runWorker里有 while 死循环一直从阻塞队列里面获取新的任务执行 getTask方法

public void run() {
    runWorker(this);
}

final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    //帮助gc回收
    w.firstTask = null;
    w.unlock(); //allow interrupts
    boolean completedAbruptly = true;
    try {
        //task 为空的情况：
        //1：任务入队列了，极限情况下，发现没有运行的线程，于是新增一个线程；
        //2：线程执行完任务执行，再次回到 while 循环。
        //如果 task 为空，会使用 getTask 方法阻塞从队列中拿数据，如果拿不到数据，会阻塞住
        while (task != null || (task = getTask()) != null) {
            //锁住 worker
            w.lock();
            //线程池 stop 中,但是线程没有到达中断状态，帮助线程中断
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            try {
                //执行 before 钩子函数，模板方法设计模式，未实现
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    //同步执行任务
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    //执行 after 钩子函数,如果这里抛出异常，会覆盖 catch 的异常
                    //所以这里异常最好不要抛出来
                    afterExecute(task, thrown);
                }
            } finally {
                //任务执行完成，计算解锁
                //这里置为null，while循环中从getTask中获取新的任务
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        //做一些抛出异常的善后工作
        processWorkerExit(w, completedAbruptly);
    }
}
```

#### （5）getTask方法（从阻塞队列中获取任务）

从阻塞队列里面获取任务 ThreadPoolExecutor.getTask 方法

``` java 
private Runnable getTask() {
    boolean timedOut = false; //Did the last poll() time out?

    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        //线程池关闭 && 队列为空，不需要再运行了，直接放回
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }

        int wc = workerCountOf(c);

        //Are workers subject to culling?
        //true  运行的线程数大于 coreSize || 核心线程也可以被灭亡
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;

        //队列以 LinkedBlockingQueue 为例，timedOut 为 true 的话说明下面 poll 方法执行返回的是 null
        //说明在等待 keepAliveTime 时间后，队列中仍然没有数据
        //说明此线程已经空闲了 keepAliveTime 了
        //再加上 wc > 1 || workQueue.isEmpty() 的判断
        //所以使用 compareAndDecrementWorkerCount 方法使线程池数量减少 1
        //并且直接 return，return 之后，此空闲的线程会自动被回收
        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }

        try {
            //从队列中阻塞拿 worker
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            //设置已超时，说明此时队列没有数据
            timedOut = true;
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
}
```

<br/>
<hr/>

## Executors（线程池工厂）

线程池工厂 java.util.concurrent.Executors

>1）**newCachedThreadPool** 创建一个可缓存线程池
>如果线程池长度超过处理需要，可灵活回收空闲线程
>若无可回收，则新建线程
>2）**newFixedThreadPool** 创建一个定长线程池
>可控制线程最大并发数，超出的线程会在队列中等待
>3）**newScheduledThreadPool** 创建一个定长线程池
>支持定时及周期性任务执行
>4）**newSingleThreadExecutor** 创建一个单线程的线程池

<br/>
<hr/>
	
## ThreadPoolExecutor 线程池的使用

一般推荐手动构造 ThreadPoolExecutor，而不是通过 Executors 工厂创建

### 1）创建线程池
``` java
//参数最多的构造方法
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler) {}
```

#### （1）corePoolSize（核心线程数）

线程池基本的工作线程数，核心线程数未满时，每提交一个任务创建一个核心线程，
当核心线程数满了的时候，每提交一个任务加入到阻塞队列，
prestartAllCoreThreads方法会提前创建所有的核心线程

#### （2）maximumPoolSize（最大线程数）
当阻塞队列满了的时候，每提交一个任务创建一个非核心线程用来执行任务，
当超过最大线程数时（保护核心线程），不再执行任务，执行拒绝策略

#### （3）keepAliveTime（线程活动保持时间）
当可以回收的线程从阻塞队列里获取任务超过此时间（阻塞队列没有任务），回收该线程

#### （4）unit（线程活动保持时间单位）
线程活动保持时间单位

#### （5）runnableTaskQueue（阻塞任务队列）
BlockingQueue的实现类，一般使用 LinkedBlockingQueue

``` java
ArrayBlockingQueue：//一个由数组结构组成的 有界阻塞 队列
LinkedBlockingQueue：//一个由链表结构组成的 有界阻塞 队列
PriorityBlockingQueue：//一个支持优先级排序的 无界阻塞 队列
DelayQueue：//一个使用优先级队列实现的 无界阻塞 队列
SynchronousQueue：//一个不存储元素的 阻塞队列
LinkedTransferQueue：//一个由链表结构组成的 无界阻塞 队列
LinkedBlockingDeque：//一个由链表结构组成的 双向阻塞 队列
```

#### （6）threadFactory（线程创建工厂）
创建线程的工厂，可以通过线程工厂给每个创建出来的线程设置更有意义的名字等

#### （7）handler（任务拒绝策略）

RejectedExecutionHandler 实现类，ThreadPoolExecutor 内部提供了 4 种

>**（1）AbortPolicy：** 直接抛出异常
>**（2）CallerRunsPolicy：** 只用调用者所在线程来运行任务
>**（3）DiscardOldestPolicy：** 丢弃队列里最近的一个任务，并执行当前任务
>**（4）DiscardPolicy：** 不处理，丢弃掉


### 2）提交任务
ThreadPoolExecutor 提供了 2 种提交任务的方法，execute()和 submit()

>**execute()方法：** 实现自 Executor 接口，用于 不需要返回值的任务
>**submit()方法：** 继承自 AbstractExecutorService 抽象类，用于 需要返回值的任务
>返回的是Future对象，使用 Future.get()方法获取

### 3）关闭线程池
ThreadPoolExecutor 提供了 2 种关闭线程池的方法，shutdown() 和 shutdownNow()

>**shutdownNow()方法：** 首先将线程池的状态设置成STOP，然后尝试停止所有的正在执行或暂停任务的线程
>**shutdown()方法：** 只是将线程池的状态设0置成SHUTDOWN状态，然后中断所有没有正在执行任务的线程

### 4）合理的配置线程池

分析任务特性，配置合适的参数

>任务的性质：CPU密集型任务、IO密集型任务和混合型任务
>任务的优先级：高、中和低
>任务的执行时间：长、中和短
>任务的依赖性：是否依赖其他系统资源，如数据库连接

（1）性质不同的任务可以用不同规模的线程池分开处理
（2）CPU密集型任务应配置尽可能小的线程，如配置 Ncpu+1 个线程的线程池
（3）IO密集型任务线程并不是一直在执行任务，则应配置尽可能多的线程，如 CPU核数 /（1 - 阻系数），阻塞系数在 0.8~0.9 之间
（4）可以通过 Runtime.getRuntime().availableProcessors()方法 获得当前设备的CPU个数
（5）阻塞队列使用有界队列

### 5）线程池的监控

通过扩展线程池进行监控
可以通过继承线程池来自定义线程池
重写线程池的 beforeExecute、afterExecute和terminated方法
也可以在任务执行前、执行后和线程池关闭前执行一些代码来进行监控
例如，监控任务的平均执行时间、最大执行时间和最小执行时间等
这几个方法在线程池里是空方法

``` java
//ThreadPoolExecutor 提供的可用于监控的字段
//还可以通过继承线程池来自定义线程池
//重写线程池的beforeExecute、afterExecute和terminated方法
//在任务执行前、执行后和线程池关闭前执行一些代码来进行监控

taskCount：线程池需要执行的任务数量
completedTaskCount：线程池在运行过程中已完成的任务数量，小于或等于taskCount
largestPoolSize：线程池里曾经创建过的最大线程数量。通过这个数据可以知道线程池是否曾经满过
如该数值等于线程池的最大大小，则表示线程池曾经满过
getPoolSize：线程池的线程数量。如果线程池不销毁的话，线程池里的线程不会自动销毁，所以这个大小只增不减
getActiveCount：获取活动的线程数
```