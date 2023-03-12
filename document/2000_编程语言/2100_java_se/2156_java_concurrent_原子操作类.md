---
title: java_concurrent_原子操作类
date: 2020-05-22 00:00:00
---
java.util.concurrent.atomic 包下面的原子操作类

## 13个原子操作类

``` java

//原子更新基本类型
AtomicBoolean
AtomicInteger
AtomicLong

//原子更新数组
AtomicIntegerArray
AtomicLongArray
AtomicReferenceArray

//原子更新引用类型
AtomicReference
//原子更新引用类型里面的字段
AtomicReferenceFieldUpdater
AtomicMarkableReference

//原子更新字段类
AtomicIntegerFieldUpdater
AtomicLongFieldUpdater
AtomicStampedReference
```

<br/>
<hr/>

## 基本原理
使用 volatile/final 修饰实际的值，用来保证 jmm 的内存语义
然后统一调用 Unsafe 类的 native 方法 compareAndSwapXXX() 方法
上面的方法在 jvm 中的具体实现是使用 Atomic::cmpxchg （cas操作），详见 jc_原子操作篇

**注意：**
基本类型，底层比较的是值
引用类型，底层比较的是对象地址 compareAndSwapObject()
```c++
// Unsafe.h
virtual jboolean compareAndSwapObject(::java::lang::Object *, jlong, ::java::lang::Object *, ::java::lang::Object *);

// natUnsafe.cc
static inline bool
compareAndSwap (volatile jobject *addr, jobject old, jobject new_val)
{
	jboolean result = false;
	spinlock lock;
  
  	// 如果字段的地址与期望的地址相等则将字段的地址更新
	if ((result = (*addr == old)))
    	*addr = new_val;
	return result;
}

// natUnsafe.cc
jboolean
sun::misc::Unsafe::compareAndSwapObject (jobject obj, jlong offset,
                     jobject expect, jobject update)
{
	// 获取字段地址并转换为字符串
	jobject *addr = (jobject*)((char *) obj + offset);
	// 调用 compareAndSwap 方法进行比较
    return compareAndSwap (addr, expect, update);
}
```