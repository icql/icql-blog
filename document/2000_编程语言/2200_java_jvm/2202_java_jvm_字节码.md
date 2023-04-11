---
title: java_jvm_字节码
date: 2020-01-20 00:00:00
---
## 字节码

java程序 -> javac编译器 -> 字节码（.class） -> java虚拟机（win/linux/...）

Write Once, Run Anywhere "一次编译，到处运行"
> 编译后生成的字节码是一个中间语言
> 而程序最后运行是以 机器码 来执行的
> java虚拟机就是用来将 **字节码** 在运行时转化为 **机器码**

<br/>
<hr/>

## 字节码数据的名词

### 类的全限定名
work/icql/jvm/bytecode/Aston是这个类的全限定名，把类全名中的 . 替换成了 /

为了使连续的多个全限定名之间不产生混淆，在使用时最后一般会加入一个 ; 表示全限定名结束

### 简单名称
指没有类型和参数修饰的方法或者字段名称

inc() 方法和 m 字段的简单名称分别是 inc 和 m

### 字段的描述符
描述字段的数据类型 

### 方法的描述符
方法的参数列表（包括数量、类型以及顺序）和返回值

按照先参数列表，后返回值的顺序描述

方法 int test(char[]c,int i) 的描述符为 ([CI)I

### 类型的表示

标识字符 | 含义
-----|---
B | 基本类型 byte
C | 基本类型 char
D | 基本类型 double
F | 基本类型 float
I | 基本类型 int
J | 基本类型 long
S | 基本类型 short
Z | 基本类型 boolean
V | 特殊类型 void
L | 对象类型，如Ljava/lang/Object;

对象类型用字符 L 加对象的全限定名来表示

数组类型，每一维度将使用一个前置的 [ 字符来描述

如 java.lang.String[][] 表示为 [[Ljava/lang/String


<br/>
<hr/>

## 字节码数据的结构

8字节为基础单位的二进制数据，整个结构用 **无符号数** 和 **表** 2种数据结构来描述，多个字节的数据结构采用大端存储
> **无符号数：** 基本数据类型，以u1，u2，u3，u4代表1，2，3，4个字节。
> **表：** 是由多个无符号数或者其他表构成的复合数据类型（整个class字节码数据本质就是一张表）

> **大端小端：**
> 数据存储以 8 bit （位）为一个 byte (字节)，比如 0xab 就是一个字节
> 存储的内容长度很可能不止 8 位，比如 java 中 int 类型占 4 个字节 32 位
> 
> 例如 0xabcd ，需要保存在4个字节里边，完整的是 0x0000abcd
> 0x0000abcd = 0xab * 0x100 + 0xcd
> 高位 -> 低位：00 -> 00 -> ab -> cd
> 
> 大端（big-endian） 是高位的放在内存低地址处，低位的放在高地址处
> 小端（little-endian） 是高位的放在内存高地址处，低位的放在低地址处
> 
> 假如 0xabcd 保存的地址是 0x100 - 0x103
> 大端：00（0x100）-> 00（0x101）-> ab（0x102）-> cd（0x103）
> 小端：cd（0x100）-> ab（0x101）-> 00（0x102）-> 00（0x103）


### 结构总览

* 可使用javap –v -p class文件查看具体信息，或者使用16进制编辑器打开class文件查看


``` java
ClassFile {
    //魔数
    //确定是否是虚拟机可以接受的class文件，值为0xCAFEBABE，
    //用作类型识别，类似于图片的字节用来标识是gif或者jpeg类型等
    u4             magic;
    
    //次版本号
    //java次版本号
    u2             minor_version;

    //java主版本号
    //jdk1.1是45.0
    //每个jdk大版本会在主版本号+1，jdk1.8是52（0x34）
    //jvm根据主版本号和次版本号来确定是否可以加载class文件，jvm只能向下兼容
    u2             major_version;

    //常量池中数据的个数
    //计数是从1开始，例如值是（0x26=38），代表有38-1=37项常量，索引值就是1-37
    //特意将0空出来，是为了某些时候需要表达的不引用任何一个常量池项目，即null
    //注意：除了常量池计数从1开始，其余计数都是从0开始
    u2             constant_pool_count;

    //常量池
    //jdk8有14种不同的表结构数据，具体的结构见下面的表，主要分为：字面量和符号引用
    //1）字面量
    //2）符号引用：
    //  类和接口的全限定名
    //  字段的名称和描述符
    //  方法的名称和描述符
    //
    cp_info        constant_pool[constant_pool_count-1];

    //类的标识
    //一共有16个标志位可以使用，当前只定义了8个，未使用到的一律为0
    u2             access_flags;
    
    //当前类在常量池中的索引
    u2             this_class;

    //父类在常量池中的索引
    u2             super_class;

    //接口个数
    u2             interfaces_count;

    //接口在常量池中的索引
    u2             interfaces[interfaces_count];

    //字段表个数
    u2             fields_count;
    //字段表信息
    //类变量、实例变量
    field_info     fields[fields_count];

    //方法表个数
    u2             methods_count;
    //方法表信息
    method_info    methods[methods_count];

    //属性表个数（属性表比较特殊，字段表和方法表都可以携带自己的属性表）
    u2             attributes_count;
    //属性表
    attribute_info attributes[attributes_count];
}
``` 


### 1）常量池

| 类型 | 描述 | 具体内容 
|---|-|--- 
| CONSTANT_Utf8_info | UTF-8 编码的字符串 | tag（u1,值为1）、length（u2,占用的字节数）、bytes（单位u1,长度为length的UTF-8编码的字符串）
| CONSTANT_Integer_info | 整形字面量 | tag（u1,值为3）、bytes（单位u4,大端存储的int值）
| CONSTANT_Float_info | 浮点型字面量 | tag（u1,值为4）、bytes（单位u4,大端存储的float值）
| CONSTANT_Long_info | 长整型字面量 | tag（u1,值为5）、bytes（单位u8,大端存储的long值）
| CONSTANT_Double_info | 双精度浮点型字面量 | tag（u1,值为6）、bytes（单位u8,大端存储的double值）
| CONSTANT_Class_info | 类或接口的符号引用 | tag（u1,值为7）、index（单位u2,指向全限定名在CONSTANT_Utf8_info的索引项）
| CONSTANT_String_info | 字符串类型字面量 | tag（u1,值为8）、index（单位u2,指向字符串字面量在CONSTANT_Utf8_info的索引项）
| CONSTANT_Fieldref_info | 字段的符号引用 | tag（u1,值为9）、index（单位u2,指向字段类型在CONSTANT_Class_info的索引项）、index（单位u2,指向字段名称在CONSTANT_NameAndType_info的索引项）
| CONSTANT_Methodref_info | 类中方法的符号引用 | tag（u1,值为10）、index（单位u2,指向声明方法的类型在CONSTANT_Class_info的索引项）、index（单位u2,指向方法描述符在CONSTANT_NameAndType_info的索引项）
| CONSTANT_InterfaceMethodref_info | 接口中方法的符号引用 | tag（u1,值为11）、index（单位u2,指向声明方法接口在CONSTANT_Class_info的索引项）、index（单位u2,指向方法描述符在CONSTANT_NameAndType_info的索引项）
| CONSTANT_NameAndType_info | 字段或方法的部分符号引用 | tag（u1,值为12）、index（单位u2,指向字段或方法名称在CONSTANT_Utf8_info的索引项）、index（单位u2,指向字段描述符或方法描述符的索引项）
| CONSTANT_MethodHandle_info | 表示方法句柄 | tag（u1,值为15）、reference_kind（单位u1,值在1~9之间,表示方法句柄的字节码行为）、reference_index（单位u2,值是对常量池的索引）
| CONSTANT_MethodType_info | 标识方法类型 | tag（u1,值为16）、descriptor_index（单位u2,指向方法描述符在CONSTANT_Utf8_info的索引项）
| CONSTANT_InvokeDynamic_info | 表示一个动态方法调用点 | tag（u1）、bootstrap_method_attr_index（u2）、name_and_type_index（u2）


### 2）类的访问标识

标志名称 | 标志值 | 含义
----|----|-----
ACC_PUBLIC | 0x0001 | 是否为 public 类型
ACC_FINAL | 0x0010 | 是否被声明为 final，只有类可设置
ACC_SUPER | 0x0020 | 是否允许使用 invokespecial 字节码的新语意，invokespecial 指令的语意在 JDK 1.0.2 之后编译出来的类这个标志都必须为真
ACC_INTERFACE | 0x0200 | 标识这是一个接口
ACC_ABSTRACT | 0x0400 | 是否为 abstract 类型，对于接口或者抽象来说，此标志值为真，其他类值为假
ACC_SYNTHETIC | 0x1000 | 标识这个类并非由用户代码产生的
ACC_ANNOTATION | 0x2000 | 标识这是一个注解
ACC_ENUM | 0x4000 | 标识这是一个枚举

### 3）字段表

包括：类变量（static修饰）和实例变量（非static修饰）

不会列出父类继承而来的字段

但有可能列出原本Java代码之中不存在的字段，如在内部类中为了保持对外部类的访问性，会自动添加指向外部类实例的字段

``` java
field_info {
    //字段访问标志
    u2             access_flags;
    //字段的名称（对常量池的引用）
    u2             name_index;
    //字段的描述符/字段的数据类型（对常量池的引用）
    u2             descriptor_index;
    //字段携带的属性表个数
    u2             attributes_count;
    //字段携带的属性表
    attribute_info attributes[attributes_count];
}
``` 

#### access_flags（字段访问标志）
标志名称 | 标志值 | 含义
-----|-----|---
ACC_PUBLIC | 0x0001 | 字段是否 public
ACC_PRIVATE | 0x0002 | 字段是否 private
ACC_PROTECTED | 0x0004 | 字段是否 protected
ACC_STATIC | 0x0008 | 字段是否 static
ACC_FINAL | 0x0010 | 字段是否 final
ACC_VOLATILE | 0x0040 | 字段是否 volatile
ACC_TRANSIENT | 0x0080 | 字段是否 transient
ACC_SYNTHETIC | 0x1000 | 字段是否由编译器自动产生的
ACC_ENUM | 0x4000 | 字段是否 enum


#### attribute_info（字段携带的属性表）
字段 final static int m = 123 （常量）
会存在一项名称为 ConstantValue 的属性，值指向常量池中的 123


### 4）方法表

如果父类方法在子类中没有被重写（Override），方法表集合中就不会出现来自父类的方法信息

但有可能会出现由编译器自动添加的方法，最典型的便是类构造器 ＜clinit＞ 方法（有static字段或static代码块才会出现）和实例构造器 ＜init＞ 方法

``` java
method_info {
    //方法访问标志
    u2             access_flags;
    //方法的名称（对常量池的引用）
    u2             name_index;
    //方法的描述符（对常量池的引用）
    u2             descriptor_index;
    //方法携带的属性表个数
    u2             attributes_count;
    //方法携带的属性表
    attribute_info attributes[attributes_count];
}
``` 

#### access_flags（方法访问标志）
标志名称 | 标志值 | 含义
-----|-----|---
ACC_PUBLIC | 0x0001 | 方法是否为 public
ACC_PRIVATE | 0x0002 | 方法是否为 private
ACC_PROTECTED | 0x0004 | 方法是否为 protected 
ACC_STATIC | 0x0008 | 方法是否为 static
ACC_FINAL | 0x0010 | 方法是否为 final
ACC_SYNCHRONIZED | 0x0020 | 方法是否为 synchronized
ACC_BRIDGE | 0x0040 | 方法是否是由编译器产生的桥接方法
ACC_VARARGS | 0x0080 | 方法是否接受不定参数
ACC_NATIVE | 0x0100 | 方法是否为 native
ACC_ABSTRACT | 0x0400 | 方法是否为 abstract
ACC_STRICT | 0x0800 | 方法是否为 strictfp
ACC_SYNTHETIC | 0x1000 | 方法是否是由编译器自动产生的

#### 方法的描述
*方法的定义：* 可以通过访问标志、名称索引、描述符索引表达清楚

*方法里的 java 代码：* 经过编译器编译成字节码指令后，存放在方法携带的属性表 Code 中

#### 方法的特征签名（区别类中方法的唯一性）
*java语言中的特征签名：* 方法名称、参数顺序、参数类型

*字节码中的特征签名：* 方法名称、参数顺序、参数类型、方法返回值、受查异常表


### 5）属性表

``` java
attribute_info {
    //属性表名称（对常量池的引用）
    u2 attribute_name_index;
    //属性表数据长度
    u4 attribute_length;
    //属性表数据
    u1 info[attribute_length];
}
``` 

#### 虚拟机规范定义的属性

属性名称 | 使用位置 | 含义
-----|------|---
Code | 方法表 | Java 代码编译成的字节码指令
ConstantValue | 字段表 | final 关键字定义的常量值
Deprecated | 类、方法表、字段表 | 被声明为 deprecated 的方法和字段
Exceptions | 方法表 | 方法抛出的异常
EnclosingMethod | 类文件 | 仅当一个类为局部类或匿名类时才能拥有这个属性，这个属性用于标识这个类所在的外围方法
InnerClasses | 类文件 | 内部类列表
LineNumberTable | Code 属性 | Java 源码的行号与字节码指令的对应关系
LocalVariableTable | Code 属性 | 方法的局部变量表
StackMapTable | Code 属性 | JDK 1.6 中新增的属性，供新的类型检查验证器（Type Checker）检查和处理目标方法的局部变量和操作数栈所需要的类型是否匹配
Signature | 类、方法表、字段表 | JDK 1.5 中新增的属性，这个属性用于支持泛型情况下的方法签名，在 Java 语言中，任何类、接口、初始化方法或成员的泛型签名如果包含了类型变量（Type Variables）或参数化类型（Parameterized Types），则 Signature 属性会为它记录泛型签名信息。由于 Java 的泛型采用擦除法实现，在为了避免类型信息被擦除后导致签名混乱，需要这个属性记录泛型中的相关信息
SourceFile | 类文件 | 记录源文件名称
SourceDebugExtension | 类文件 | JDK 1.6 中新增的属性，SourceDebugExtension 属性用于存储额外的调试信息。譬如在进行 JPS 文件调试时，无法通过 Java 堆栈来定位到 JSP 文件的行号，JSR-45 规范为这些非 Java 语言编写，却需要编译成字节码并运行在 Java 虚拟机中的程序提供了一个进行调试的标准机制，使用 SourceDebugExtension 属性就可以用于存储这个标准所新加入的调试信息
Synthetic | 类、方法表、字段表 | 标识方法或字段为编译器自动生成的
LocalVariableType | 类 | JDK 1.5 中新增的属性，它使用特征签名代替描述符，是为了引入泛型语法之后能描述泛型参数化类型而添加
RuntimeVisibleAnnotations | 类、方法表、字段表 | JDK 1.5 中新增的属性，为动态注解提供支持。RuntimeVisibleAnnotations 属性用于指明哪些注解是运行时（实际上运行时就是进行反射调用）可见的
RuntimeInvisibleAnnotations | 类、方法表、字段表 | JDK 1.5 中新增的属性，与 RuntimeVisibleAnnotations 属性作用刚好相反，用于指明哪些注解是运行时不可见的
RuntimeVisibleParameterAnnotations | 方法表 | JDK 1.5 中新增的属性，作用与 RuntimeVisibleAnnotations 属性类似，只不过作用对象为方法参数
RuntimeInvisibleParameterAnnotations | 方法表 | JDK 1.5 中新增的属性，作用与 RuntimeInvisibleAnnotations 属性类似，只不过作用对象为方法参数
AnnotationDefault | 方法表 | JDK 1.5 中新增的属性，用于记录注解类元素的默认值
BootstrapMethods | 类文件 | JDK 1.7 中新增的属性，用于保存 invokedynamic 指令引用的引导方法限定符


#### ConstantValue属性

主要的作用是在 **类加载的准备阶段** 为 static final 字段（基本类型和String）赋值

``` java
ConstantValue_attribute {
    //常量名（对常量池的引用）
    u2 attribute_name_index;
    //占用的长度，固定为2
    u4 attribute_length;
    //对应的字面值在常量池中的索引
    u2 constantvalue_index;
}

只支持（基本类型和String）9种类型
String（CONSTANT_String_info）
boolean，byte，char，short，int（CONSTANT_Integer_info），共用常量池中的这个类型
long（CONSTANT_Long_info）
float（CONSTANT_Float_info）
double（CONSTANT_Double_info）

``` 

### 6）static final 常量理解

static final 修饰的 **基本类型和String** 9种类型字段，并且后面的赋值是常量（字面量）

```
> static final int MAX = 100;  常量
> static final int RANDOM = new Random().nextInt();  非常量
> static final String STR_0 = "str";  常量
> static final String STR_1 = new String("str");  非常量
```

在编译期就会将 **常量的字面量 编译到 使用了此常量的类的相关字节码中替代原来的引用**

``` java
class A{
    public static final int NUMBER = 100;
}

class B{
    public void test(){
        System.out.println(A.NUMBER);
    }
}

//编译后，B类的class文件中的test方法的字节码中直接变成了100这个常量
//也就是说此时的 B 和 A 无依赖关系
``` 


<br/>
<hr/>

## 字节码数据的指令

字节码指令类型：操作码Opcode、操作数Operands

https://www.jianshu.com/p/1ad68e2b8cc1

<br/>
<hr/>

## 虚拟机字节码执行引擎
TODO

