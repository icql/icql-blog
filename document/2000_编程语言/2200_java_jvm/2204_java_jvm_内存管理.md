---
title: java_jvm_内存管理
date: 2020-04-14 00:00:00
---

jvm内存管理采用的是 **自动管理** 的机制

<br/>
<hr/>

## 运行时内存区域

### 1）程序计数器（Program Counter Register）

**线程私有，每个线程都有一个程序计数器**

> **（1）异常：**
> 唯一一个不会抛出 OutOfMemoryError 的区域
> 只存储下一个字节码指令的地址，消耗内存小且固定，所以不会oom
> 
> **（2）用途：**
> 可看作是当前线程所执行的字节码的行号指示器
> 记录正在执行的虚拟机字节码指令的地址（如果执行的是 native 方法，计数器的值置为空）
> 分支、循环、异常、调整、线程恢复等都依赖此区域

### 2）栈（Stack）

**线程私有，HotSpot将 本地方法栈 和 虚拟机栈 合二为一**

> **（1）异常：**
> StackOverflowError
> OutOfMemoryError
>
> **（2）用途：**
> 虚拟机栈：描述的是 java 方法执行的内存模型
> 本地方法栈：描述的是 native 方法执行的内存模型

### 3）堆（Heap）

**进程私有**

> **（1）异常：**
> OutOfMemoryError
>
> **（2）用途：**
> 主要用于存放java对象实例，GC管理的主要区域，不需要连续的内存


### 4）方法区（Method Area）

**进程私有**

> **（1）异常：**
> OutOfMemoryError
>
> **（2）用途：**
> 主要用于存放类型元数据信息等，jdk1.8 使用 Metaspace（元空间）实现方法区

### 5）直接内存（Direct Memory）

不属于jvm管理的内存区域

> **（1）异常：**
> OutOfMemoryError
>
> **（2）用途：**
> nio有相关缓冲区放在此区域

<br/>
<hr/>

## 堆中java对象的内存布局

### 1）创建对象
> （1）首先检查 new 指令参数 **类型** 是否已被加载，若没有执行类型的加载
> （2）jvm为对象在堆中分配内存（TLAB详细介绍参见垃圾回收篇）
> （3）对象的实例字段初始化为0值
> （4）对象头设置（Object Header）
> （5）执行实例构造方法（编译后的字节码，已将字段赋值添加到每个构造方法的最前面）

``` java
//类Aston
public class Aston {

    private int num = 100;

    public static int s_num = 100;

    public static final int s_f_num = 1000;

    static {
        System.out.println("类初始化");
    }

    public Aston() {
    }

    public Aston(int num) {
        this.num = num;
    }

    public static void test(){
        Best best = new Best();
    }
}

//类Aston.class字节码，javap -v -p Aston.class
//可以看到 2 个构造方法的最前面（第5、7行）都加入了字段 num 的赋值操作
  public work.icql.jvm.classload.Aston();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: aload_0
         5: bipush        100
         7: putfield      #2                  // Field num:I
        10: return
      LineNumberTable:
        line 15: 0
        line 5: 4
        line 16: 10
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      11     0  this   Lwork/icql/jvm/classload/Aston;

  public work.icql.jvm.classload.Aston(int);
    descriptor: (I)V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: aload_0
         5: bipush        100
         7: putfield      #2                  // Field num:I
        10: aload_0
        11: iload_1
        12: putfield      #2                  // Field num:I
        15: return
      LineNumberTable:
        line 18: 0
        line 5: 4
        line 19: 10
        line 20: 15
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      16     0  this   Lwork/icql/jvm/classload/Aston;
            0      16     1   num   I
```


### 2）对象内存布局（64位虚拟机）

#### （1）对象头
> **Mark Word：** 8个字节，主要用于存放 hashcode，GC分代年龄、锁状态标志、线程持有锁 等等（具体分析查看 jc_并发关键字篇 synchronized）
> **Klass Word：** 8个字节，指向类元数据的指针
> **数组长度**：8个字节，普通java对象可以通过类型元数据确定所需内存大小，但是数组不行，必须要记录数据元素的长度

#### （2）实例数据
> **存储的数据：** 所有的实例字段的数据（包括父类的实例字段）
> **存储的顺序：** 
>  优先存储父类字段，默认按照源码中定义的顺序存储
>  还受以下两点的影响：
>  （1）jvm默认分配按类型顺序排序：longs/doubles，ints，shorts/chars，bytes/booleans，oops（对象的指针）
>  （2）jvm参数 UseCompressedOops 指针压缩，CompactFields 字段间隙填充，默认都是 true，https://juejin.im/post/6844903768077647880
> **存储的大小：**
>  （1）基本类型：数据类型的大小
>  （2）引用类型：引用64位jvm占用8个字节（开启指针压缩后，默认开启，占用4个字节），32位jvm占用4个字节

#### （3）对齐填充
> 不一定存在，起占位符作用
> 因为 **jvm要求对象的大小必须时8字节的倍数**
> 所以当前面的部分不够时，用以对齐


### 3）对象访问定位
> 1）直接访问：栈中存储实例对象的实例指针（hotspot采用此种方法）
> 2）间接访问：栈中存储实例对象的句柄的指针--实例对象的句柄存储实例对象的指针--实例对象存储类型指针
> 
> **比较：** 
>  直接访问速度较快，只有 1 次定位，而间接访问需要 2 次定位
>  当垃圾回收时，实例对象的地址会改变，需要同步变更指向此实例对象的所有的栈中存储的指针
>  而间接访问则不需要，只需要修改句柄中的指针

<br/>
<hr/>

## 方法区中的运行时常量池（元数据空间）

https://segmentfault.com/a/1190000018792105

### 1）类型常量池（每个类型都有一个）
类型加载后，每一个类型的元数据 InstanceKlass 都会引用着一个自己的 ConstantPool 类型的运行时常量池
基本上和 **class字节码数据的静态常量池** 保持一致，将静态常量池转化为运行时常量池
只不过，其中的符号引用会在运行过程中逐渐被解析为对应的直接引用（指针）

### 2）符号常量池（SymbolTable，全局共享）

HashTable结构，不会自动扩容

全局的 **SymbolTable** 管理着 **每个类型元数据的常量池** 里的 CONSTANT_Utf8_info 类型的常量


### 3）字符串常量池（StringTable，全局共享）

HashTable结构，不会自动扩容，会进行垃圾回收（具体分析查看垃圾回收篇）

> String对象本身还是存储在堆中，**StringTable字符串常量池中保存的是其引用**
> -XX:+PrintStringTableStatistics ，进程关闭时打印 SymbolTable 和 StringTable 统计值
> -XX:StringTableSize ，设置 StringTable 桶的个数，jdk8默认值为 60013

> 运行时 **字符串对象的引用** 入池的时机：
> 1）当遇到 ldc 指令，参数为类型元数据常量池中的字符串时，就会将此字符串的引用（putIfAbsent）放入常量池中
> 2）String对象调用 intern() 方法时，就会将其引用（putIfAbsent）放入常量池中