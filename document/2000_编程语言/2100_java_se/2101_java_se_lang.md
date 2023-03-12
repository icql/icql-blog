---
title: java_se_lang
date: 2018-03-31 00:00:00
---

## 基础

### 1）jdk、jre
classpath：.class文件运行路径，默认为.，即当前目录；
可手动设置 set classpath=d:\ 

### 2）数据类型

#### （1）基本类型
均有默认值：整数类型默认值为0，浮点类型默认值为0.0，字符为\u0000，布尔为flase；
普通方法里的声明类型后的未赋值变量，不能使用
类的字段变量未赋值，在new出的对象可以使用，其值为默认值

**类型：**
>**整数类型：**（范围为正负）
>byte（字节）：8bit，2^8=256个，-128~127
>short（短整型）：16bit，2^16=65536
>int（整型）：32bit，默认整数类型都是int，范围为+-21亿级
>long（长整型）：64bit
>
>**浮点类型：**（范围为正负，不精确，会造成数据丢失） 
>float（单精度）：32bit
>double（双精度）：64bit，默认小数类型都是double
>char（字符）：16bit（范围为正0~2^16=65536），''单引号，采用Unicode编码格式，表示一个Unicode字符，所有字符都用两个字节表示，包括中英文，
>boolean（布尔）:不确定，看具体虚拟机实现

**溢出：** 数据超过了其表示的范围，会循环表示

**转型：**
>自动转型：范围由小到大，byte，short，int，long，float，double
>强制转型：由大到小，（），F

#### （2）引用类型
类型，数组

#### （3）值传递还是引用传递
java是值传递

**值传递和引用传递的关键区别？**
在调用方法前、后，值传递不会改变变量的值，而引用传递会改变

``` java
//方法调用，传参相当于给变量赋值的操作

//（1）
void foo(int value) {
    value = 100;
}
foo(num); 
//num在方法调用前后没有改变

//（2）
void foo(String text) {
    text = "windows";
}
foo(str); 
//str在方法调用前后没有改变
//没有改变指的是str变量本身存储的值，即对象地址

//（3）
void foo(StringBuilder builder) {
    builder = new StringBuilder("ipad");
}
StringBuilder sb = new StringBuilder("iphone");
foo(sb); 
//sb在方法调用前后没有改变
//sb存储的值都是原来iphone对象的地址

//（4）
void foo(StringBuilder builder) {
    builder.append("4");
}
StringBuilder sb = new StringBuilder("iphone");
foo(sb); 
//sb在方法调用前后没有改变
//sb存储的值都是原来iphone对象的地址
//但是那个对象的值变了

```


### 3）运算符
&& 和 & , || 和 |区别：
与和或，表示意义一样，双字符有短路功能，检查条件时，前后条件如果前条件满足，后条件直接跳过不检查了，而单字符则都要检查

### 4）程序结构
顺序结构
选择结构
循环结构：foreach功能，for（int item : int[]类型变量）

<br/>
<hr/>

## 面向对象

### 1）3大特征
封装（数据抽象），继承，多态（方法重载，对象多态:子父对象互转）

**多态：**
>（1）支持继承
>（2）支持子类重写父类方法
>（3）支持父类引用指向子类对象
>java使用这3种机制的来实现多态


### 2）类型
某类群体的一些基本特征的抽象的概念集合，属于模板；对象则是具体的实例；接口、枚举、注解等都是特殊的类

#### （1）访问权限控制
**类型：**
public，default（默认为空）

**类型成员：**
>public：可以被所有其他类所访问
>protected：自身、子类及同一个包中类可以访问
>default：同一包中的类可以访问，声明时没有加修饰符，认为是friendly
>private：只能被自己访问和修改
    
#### （2）构造方法

隐式的是static，名称与类名相同，没有返回值（直接不写返回值类型），一个类中至少有一个构造方法，若缺省，则自动生成一个无参构造方法，若自己定义了构造方法，则不会自动生成

#### （3）关键字

* static：所有类的实例对象和类共享同一个static成员
    * 字段：表示类变量
    * 方法：表示类方法
* final：表示不再改变
    * 类：禁止继承此类，由于类是final，则它的方法隐式的是final
    * 字段：一旦赋值则不再允许改变，对于基本类型表示永不改变的编译时常量；对于引用类型指的是这个引用只能指向这个对象，不能改变其指向，但是可以改变它指向的这个对象的值
    * 方法：禁止重写方法，private方法其实隐式的是 final
* static final：常量
* this：类内部方法中经常使用，作为当前类的当前实例，使用方法
    * 当前对象：this
    * 当前字段：this.字段名
    * 当前类方法：this.普通方法，this()代表构造方法，多个构造方法时调用其中的构造方法，this()；必须放在方法的首句
* super：
    * super.字段名，父类字段
    * super()，调用父类构造方法，缺省默认会调用父类无参构造方法，同this()一样，必须放在构造方法中的第一行，和this()互相调用的话，至少要保留一个构造方法没有调用this()作为出口，因为这个出口要用来调用父类super()

#### （4）类之间的关系（6种）
	
``` java
//1）泛化（Generalization）可以简单理解为继承关系
public class A { ... }
public class B extends A { ... }

//2）实现（Realization）一般是指接口和实现类之间的关系
public interface A {...}
public class B implements A { ... }

//3）聚合（Aggregation）是一种包含关系，A 类对象包含 B 类对象，B 类对象的生命周期可以不依赖 A 类对象的生命周期，
//也就是说可以单独销毁 A 类对象而不影响 B 对象，比如课程与学生之间的关系
public class A {
  private B b;
  public A(B b) {
    this.b = b;
  }
}

//4）组合（Composition）也是一种包含关系。A 类对象包含 B 类对象，B 类对象的生命周期跟依赖 A 类对象的生命周期，
//B 类对象不可单独存在，比如鸟与翅膀之间的关系
public class A {
  private B b;
  public A() {
    this.b = new B();
  }
}

//5）关联（Association）是一种非常弱的关系，包含聚合、组合两种关系。具体到代码层面，
//如果 B 类对象是 A 类的成员变量，那 B 类和 A 类就是关联关系


//6）依赖（Dependency）是一种比关联关系更加弱的关系，包含关联关系。
//不管是 B 类对象是 A 类对象的成员变量，还是 A 类的方法使用 B 类对象作为参数或者返回值、局部变量，
//只要 B 类对象和 A 类对象有任何使用关系，我们都称它们有依赖关系。

```


#### （5）主方法
public static void main(String args[]){}
>public主方法必须是公共的
>static主方法由类直接调用，执行类：java类名称
>void主方法是一切的开始，没有返回值
>main系统规定的主方法名称，执行类默认找到此名称
>String args[]表示一些运行时的参数，通过字符串接收，在类名后 空格+字符串参数，多个参数用空格区分开，均保存在args[]字符串数组中

#### （6）代码块
程序中用'{}'定义起来的程序
>普通代码块，方法里用于区分代码
>构造代码块，（{语句;}），将和所有构造器分别合并为各自的构造器，相当于为实例对象统一初始化
>静态代码块，static {//静态代码块;}，和编译时将和static字段合并相当于类的构造器
		
#### （7）特殊类型
**（1） 抽象类**
* 普通类是完整的功能类，可直接产生对象使用，其中方法已经实现完整；而抽象类则只声明了方法而未具体实现，所以要加前缀abstract，抽象方法一定要在抽象类里边，抽象类也用abstract声明
* 抽象类不能实例化
* 抽象类必须有子类，使用extend继承，所以不能有final定义，final定义类表示不能被继承（终结）
* 子类必须重写抽象类的全部抽象方法
* 抽象类对象可以使用子类对象的向上转型方式
* 抽象类有构造方法，因为除了抽象方法，普通的字段和方法要实例化
* 抽象类可以不包含抽象方法

**（2）接口interface**
* 极度抽象，可以实现"多继承"，让实现类可以向上转型为多种不同的类型，接口本身也可以多继承形成新的接口
* 可以包含字段，而这些字段默认是 public final static，java SE5之前用来 创建全局枚举变量 enum，不能是空白final，必须初始化
* 接口中方法默认是 public abstrct，而接口本身默认是default
* 默认接口的访问修饰符是public（一般不写），接口内部的成员访问修饰符也只能是public

**（3）枚举enum（多例）**

**（4）注解Annotation（标记）**
java SE 中的三个注解
>@Override 重写检查
>@Deprecated 提醒过期的方法，不建议使用
>@SuppressWarnings 压制警告信息

``` java
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation1 {
}

(1) @interface
使用 @interface 定义注解时
意味着它实现了 java.lang.annotation.Annotation 接口
即该注解就是一个Annotation
定义 Annotation 时，@interface 是必须的
注意：它和我们通常的 implemented 实现接口的方法不同
Annotation 接口的实现细节都由编译器完成
通过 @interface 定义注解后
该注解不能继承其他的注解或接口

(2) @Documented
类和方法的 Annotation 在缺省情况下是不出现在 javadoc 中的
如果使用 @Documented 修饰该 Annotation，则表示它可以出现在 javadoc 中
定义 Annotation 时，@Documented 可有可无
若没有定义，则 Annotation 不会出现在 javadoc 中

(3) @Target(ElementType.TYPE)
前面我们说过，ElementType 是 Annotation 的类型属性
而 @Target 的作用，就是来指定 Annotation 的类型属性
@Target(ElementType.TYPE) 的意思就是指定该 Annotation 的类型是 ElementType.TYPE
这就意味着，MyAnnotation1 是来修饰"类、接口（包括注释类型）或枚举声明"的注解
定义 Annotation 时，@Target 可有可无
若有 @Target，则该 Annotation 只能用于它所指定的地方
若没有 @Target，则该 Annotation 可以用于任何地方

(4) @Retention(RetentionPolicy.RUNTIME)
RetentionPolicy 是 Annotation 的策略属性
而 @Retention 的作用，就是指定 Annotation 的策略属性
@Retention(RetentionPolicy.RUNTIME) 的意思就是
指定该 Annotation 的策略是 RetentionPolicy.RUNTIME
这就意味着编译器会将该 Annotation 信息保留在 .class 文件中，并且能被虚拟机读取
定义 Annotation 时，@Retention 可有可无
若没有 @Retention，则默认是 RetentionPolicy.CLASS
```

**（5）内部类：在一个类的内部定义了一个类**
* 普通内部类：普通内部类的对象默认持有包含类的一个对象的引用
* 静态内部类：static，相当于何包含类没有任何关系

``` java
interface Message{
    public void print();
}
class Demo{
    public static void get(Message msg){
        msg.print();
    }
}
public class TestDemo{
    public static void main(String[] args){
        Demo.get(new Message(){
            public void print(){
                System.out.println("Hello World");
            }
        })
    }
}
```


#### （8）异常错误 Throwable

##### （8.1）Error extends Throwable：
指的是jvm错误，运行环境错误或硬件错误，并非程序错误

##### （8.2）Exception extends Throwable：
指的是程序中出现的错误，可以进行异常处理，可以使用Exception类接受所有异常
RuntimeException 运行时异常（不受检查的异常），Exception的特殊子类
    
    
#### （9）泛型
>通配符 ?
>
>（1）设置上限
>类定义：类名称<T extends 类>{}
>声明对象：类名称<? extends 类> 对象名
>
>（2）设置下限
>类定义：类名称<T super 类>{}
>声明对象：类名称<? super 类> 对象名

>泛型类、泛型接口
>泛型方法：pulbic static <T> T[] get(T ... args){}，先要 在前面声明<T>泛型标记，否则无法使用


#### （10）其他语法特性

``` java
* 包：类似于命名空间，为了统一管理并区分类
* 访问修饰符
    * 类（public 和 friendly默认不写）：public任意，friendly只能在同一包中访问
    * 类成员：
        * public（公共访问控制符），指定该变量为公共的，他可以被任何对象的方法访问
        * private（私有访问控制符）指定该变量只允许自己的类的方法访问，其他任何类（包括子类）中的方法均不能访问
        * protected（保护访问控制符）指定该变量可以别被自己的类和子类访问。在子类中可以覆盖此变量
        * friendly ，在同一个包中的类可以访问，其他包中的类不能访问
* 常用命令：
    * javac -d . Test.java  -d代表生成文件夹，. 代表当前目录，Test.java 代表java源文件，生成"包.类文件夹形式"
    * 运行 java 包.类
    * 将"包.类文件夹形式"压缩成.jar文件，jar -cvf my.jar 文件夹名称

* 断言assert:调试使用
* 不定项参数：使用 (int ... data)，data实际是数组，在方法里以数组使用，且不定项参数必须放参数列表最后一个
* foreach：使用语法 for(数据类型 变量 : 数组或者集合){}
* 静态导入：如果要导入的包.类的全部方法是static，则可以使用 import static 包.类.*
```


## 引用逃逸
对象引用逃逸：指对象在构建完成前（constructor执行完之前）就被其他对象引用（多线程环境下）
解决：可以使用单例或者使用同步关键字

``` java
//创建Test对象的线程 和 其他线程读取test字段，就可能发生对象引用逃逸

public class Test {
    
    public static Test test;
    
    private int i;
    
    private final int j;

    public Test(int i, int j) {
        test = this;
        this.i = i;
        this.j = j;
    }
}
```