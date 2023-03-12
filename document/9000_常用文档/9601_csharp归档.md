---
title: csharp归档
date: 2019-03-15 00:00:00
---


## clr-托管模块
* 1.理解CLR
    * 含义：CLR是指微软.NET Framework框架的公共语言运行时(Common Language Runtime，简称CLR)，简单的理解CLR是指所有[托管代码]运行时的宿主，也就是说托管代码的运行是依赖(寄宿)于CLR环境，所以使用托管代码编写的程序，正确运行的必要条件是用户必须安装.NET Framework(CLR作为其中一部分由此提供）
    * 原理：CLR类似于JAVA的JVM(JAVA虚拟机)，.NET与JAVA都一个样，编译后都不是native code(机器码0和1)，它们分别是.NET为IL中间语言，JAVA为字节码，然后分别由.NET的CLR，JAVA的JVM去解析编译native code
    * 功能：如内存管理、程序集加载、安全性、异常处理和线程同步等；CLR只对高级语言公开一部分功能，对IL语言开放了所有功能，所以可使用IL语言编写程序使用不对高级语言公开的那部分功能

* 2.托管与非托管代码
    * 托管：C#，VB.NET，J#等.NET平台语言(这些语言的编译器编译生成托管模块（包含IL代码），再由CLR编译生成native code)
    * 非托管：C/C++语言(C/C++编译器编译直接生成native code，注意C++编译器默认生成非托管即native code，但是允许同时编写托管和非托管代码，非常强大)

* 3.托管模块与程序集
    * 托管模块：后缀名.netmodule，是标准的PE(Portable Executable，可移植执行体)文件，分为(PE32)32位和(PE32+)64位，这两种文件均需要CLR才能执行，其组成部分：
        * PE32或PE32+头：两种格式区分32位和64位，标识了文件类型(GUI[Graphical User Interface]，CUI[Command User Interface]，DLL)，文件生成时间标记等
        * CLR头：CLR版本，托管模块自身的元数据、资源、强名称，一些标志等
        * 元数据(metadata)：二进制数据块；总共分为三种元数据表：清单表，定义表，引用表；一般托管模块只有两种元数据表(定义表和引用表)，主托管模块则包含三种表
            * 清单表manifest table：基于当前程序集的；程序集定义，文件定义，资源文件定义，公开类型定义(public类型)；注意 包含清单表的文件还包含一个引用表，用来提供当前程序集中对外引用的所有程序集入口
            * 定义表definition table：基于当前模块中的；托管模块定义表(模块的文件名，后缀名，版本ID等)，类型定义表，方法定义表，字段定义表，参数定义表，属性定义表，事件定义表(当前模块中类的成员包括方法和属性的内部实现用IL代码描述，字段常量之类的用元数据就可以描述出来了)
            * 引用表reference table：基于当前模块中的；程序集引用表，同程序集的其他模块引用表，类型引用表，成员引用表；主要是提供引用的入口

        * IL(intermediate language中间语言)代码：编译器编译源代码生成的代码，在运行时，CLR将IL编译成本机CPU指令；IL语言是和CPU无关的机器语言，比大多数机器语言高级，可视为面向对象的机器语言，可访问操作对象类型，抛出捕获异常等
    * 程序集：后缀名.dll或.exe，是由一个/(多个模块+资源文件)的逻辑性分组，是重用、安全性以及版本控制的最小单元，在CLR中程序集相当于组件
        * 分类：单文件程序集和多文件程序集；单文件程序集只有一个托管模块(即为主托管模块，包含清单元数据表)，多文件程序集有多个托管模块文件和资源，但只有一个主托管模块(包含清单元数据表)
        * C#编译器默认将生成单文件程序集(首先生成一个托管模块，再将其转换为程序集)，VS只能通过C#编译器生成单文件程序集，要生成多文件程序集必须使用程序集链接器AL.exe

* 4.省略
    * 程序集的版本信息以及文件属性 
    * CLR对IL验证
    * 不安全代码 unsafe关键字
    * FCL(Framework Class Library)Framework类库
    * CTS(Commmon Type System)通用类型系统
    * 托管语言和非托管语言的可互操作性
    * 应用程序部署以及分目录安装程序

## 类型
* 1.类型基础
    * System.Object
        * 所有类型都从System.Object派生
        * public(方法：ToString，Equals，GetHashCode，GetType，属性：无)
        * protect(方法：MemberwiseClose，Finalize垃圾回收相关，属性：无)
        * CLR要求所有对象用 new 操作符创建
            * 值类型：new 操作符所做的事情：
                * 1）直接将实例存储在栈上，并且初始化 字段为0
            * 引用类型 new 操作符所做的事情：
                * 1）计算对象所需字节数，包括该类型及其基类型定义的所有实例字段所需的字节数和类型对象指针、同步块索引所需字节数，类型指针和同步块索引是CLR用来管理对象的
                * 2）在托管堆上分配该对象所需内存空间
                * 3）初始化类型对象指针和同步块索引
                * 4）执行构造函数。大多数编译器都在构造函数中自动生成一段代码调用基类构造函数，每个类型的构造函数在执行时都会初始化该类型定义的实例字段(若构造函数未初始化，默认字段为引用类型初始化为null，值类型为0)
                * 5）返回指向新建对象的一个引用，保存在对象变量中
        * 对象和集合初始化器（注意：匿名类型，匿名对象）
        ```csharp
        Student s=new Student{Age=12,Name="wo"};
        List<string> list=new List<string>{"","",""};
        ```
        * 类型安全：利用GetType()方法总是可以知道对象的类型；GetType()非虚(virtual)
    * 类型转换
        * 派生类与基类的关系：is a kind of
        * 派生类可转为基类，基类不可转为派生类(is a kind of，转换-用作)
        * 强制转换：(类型)变量
        * is 检查对象是否兼容某一类型，返回 Boolean (true or false，对象的基类型和本身类型true) 
        ```csharp
            /*检查2次类型*/
            if(a is Employee)
                Employee b = (Employee)a;
        ```
        * as 强制转类型，返回对象的非null引用;如果可以转返回引用，不可以转返回null
        ```csharp
            /*检查1次类型*/
            Employee b = a as Employee;
            if (b != null)
        ```
    * 命名空间和程序集
        * 命名空间是类型的逻辑性分组，程序集和命名空间不一定相关，即一个命名空间可以出现在多个程序集中
        * C# using指令(前缀，不一定只是命名空间)，简化代码增强可读性，但遇到相同前缀不同类型时为避免歧义，须在代码中写完整前缀以区分类型，还可以在using指令写入代名，例如 using xxx=命名空间
        * 编译器编译源代码时，首先在代码中查找类型，若找不到依次加载using指令前缀在引用的程序集中查找，直到找到类型
        * 程序集中的类型定义元数据表中的类型时完整的类型名称，CLR看到的是完整的实际类型名称
* 2.类型介绍
    * 基元类型：编译器直接支持的数据类型，与FCL类型相关联，简化操作
        * 常用的有string（System.String），object（System.Object），dynamic（System.Object），int（System.Int32），long（System.Int64），char，float，double，bool，decimal，byte等（无符号类型未列出）
        * checked 和 unchecked 基元类型操作：溢出检查（省略）
    * FCL类型分类：值类型（整型:Int，长整型:long，浮点型:float，字符型:char，布尔型:bool，枚举:enum，结构:struct，枚举:enum，DateTime也是值类型），引用类型(class)
        * 值类型直接将实例存储在栈上，继承关系：结构体-System.ValueType(重写了Equals和GetHashCode方法)-System.Object，枚举-System.Enum-System.ValueType（重写）-System.Object
        * 引用类型指针存储在栈上，实例存储在堆上，继承关系：类-System.Object
        * 设计类型时须慎重选择是使用值类型还是引用类型（详见NET CLR via C# p108，第5章5.2）
    * 值类型的装箱和拆箱
        * 泛型集合类（System.Collections.Generic.List<T>类）操作值类型时不需要进行装箱拆箱，所以优先使用泛型集合类
        * 装箱：值类型转引用类型
            * 基本转化原理：在托管堆分配内存（值类型的字段和所有对象都有的类型对象指针和同步块索引所需的大小），将值类型的字段复制到新分配的堆内存，返回对象地址，此时值类型变成了引用类型
            * C#编译器自动生成对值类型进行装箱所需的IL代码，一般是检测到代码向要求引用类型的方法传递值类型，就会自动装箱
            * 同一个值类型装箱几次就在堆内存分配几个对象，所以要避免多次装箱，多留意方法传参的自动装箱
        * 拆箱：获取值类型在托管堆内存的指针（引用地址），所以拆箱的代价比装箱低得多
            * 拆箱时，只能转型为最初未装箱的值类型，如果需要更改最后的类型，则需要另外转换
    * Equals()方法的讨论
        * 引用类型的Equals()方法默认继承自System.Object，判断的是同一性而非相等性，若两变量指针指向同一地址则返回true
        * 值类型的Equals()方法默认继承自System.ValueType重写过后的，所以是判断实际值是否相等，是相等性
        * 判断同一性：应该使用System.Object的静态方法ReferenceEquals()方法，因为不知道对象的Equals()是否重写过
        * 判断相等性：引用类型若需要判断相等性，可以重写Equals方法和==和!=操作符方法，具体查看本书的第5章5.3.2
    * 对象哈希码
        如果重写了Equals()就要重写GetHashCode()，哈希码算法用来比较对象的相等性，具体查看5.4
    * dynamic，object，var比较说明
        * 参考http://www.cnblogs.com/JustRun1983/p/3163350.html 具体查看5.5
        * 三种都可以当作任意类型来赋值。dynamic和object本来就是基元类型，代表System.Object，所有对象继承于此，所以可以；var不是类型，在var声明变量的那一刻就决定了它是什么类型，编译成IL代码就会转化为实际类型，所以编译时即可检查错误，其他两种都是在执行时才能发现错误
* 3.类型的修饰符
    * 1）访问修饰符：
        * public：无限制
        * internal（缺省）：同一程序集可访问
        * 当希望类型为internal并且可以让指定的程序集可访问，可使用友元程序集（friend assembly）详见6.3
    * 2）static修饰符(默认 无): 只能应用于引用类型，不能应用于值类型，CLR总是允许值类型实例化
        * 静态类不能实例化，所以不能有实例构造函数，但有静态构造函数，例如System.Math类
        * 静态类必须直接从基类System.Object派生，从其他类继承没有意义，继承只适用于对象，而静态类不能创建实例，也不可被继承（密封的sealed）
        * 仅包含静态成员，不能包含实例成员
    * 3）继承修饰符：只能应用于引用类型，不能应用于值类型
        * abstract抽象类: 创建仅用于继承用途的类，用途是提供多个派生类可共享的基类的公共定义，
            * 不能实例化，可被继承
            * 抽象类可以不含抽象方法，但有抽象方法的类一定是抽象类
        * sealed密封类: 主要用于防止派生
            * 不可被继承，可实例化
    * 4）分部修饰符partial：允许对类型（类，结构，接口都可以）的代码分布到不同的文件中，C#编译器编译时会自动合并代码，CLR对此一无所知
* 4.类型成员：字段（包括常量），方法（包括属性，构造器，事件）
* 5.特殊类型（接口和委托）

## 类型成员
* 1.类型成员分类：字段（包括常量），方法（包括属性，构造器），事件
* 2.类型成员的修饰符	
    * 1）访问修饰符：
        * public：（interface成员缺省）无限制
        * private：（class，struct成员缺省）类型内部可访问（实例不能访问）
        * internal：同一程序集可访问
        * protected：类型内部和继承的子类可访问（实例不能访问）
        * protected internal
    * 2）static修饰符(默认 无):类型成员和实例成员 
        * 访问:应使用类名而不是变量名来指定该成员的位置，类名.静态成员名
        * 静态成员属于类，不属于实例对象
        * 静态成员不能被继承
    * 3）继承修饰符：只有引用类型的方法可以使用（构造函数除外）
        * abstract: 访问修饰必须是public，抽象方法没有实现，所以方法定义后面是分号，而不是常规的方法块{}，抽象类的派生类必须实现所有抽象方法；有抽象方法的一定是抽象类
        * virtual和override：派生类重写父类同名方法
            * 父类方法virtual修饰(此时访问修饰符不能是private)
            * 子类方法用new修饰，则隐藏了父类的方法，父类方法仍然存在
            * 子类方法用override修饰，则重写了父类的方法，父类方法将不存在
            * sealed用于子类重写了父类的方法后，防止之后孙子类继承子类重写该方法
        * new：派生类隐藏父类同名方法
            * 派生类实例调用此方法时是调用的new方法，基类的同名方法依然存在
* 3.类型成员详述
    * 1）字段：类型的状态，存储着类型满足其设计所必须拥有的数据，一般为private，便于封装数据，字段类型可以是值类型或引用类型
        * readonly（只读，只能在构造器或字段定义时（又叫字段的内联初始化）设置其值，但是可以利用反射来修改其值http://blog.csdn.net/puncha/article/details/8462740）
        * volatile（与基元线程有关，详见线程）
        * 字段是引用类型时，readonly是不可改变引用（指针的地址），而不是值
        * 常量（特殊字段）：只数据值恒定不变的符号，逻辑上隐式的是静态的（所以不允许加前缀static），属于类型，不属于实例
            * 常量的类型只能是基元类型（stirng，int之类，虽然也允许使用非基元类型但必须设置其值为null，毫无意义）
            * 常量在编译后存放在元数据字段定义表中，常量的实际值直接嵌入在用到它的IL代码中，运行时不分配任何内存，不能获取其地址，也不能以引用方式传递常量
            * 常量有诸多限制，要想获取新值，相关联的程序集都必须重新编译，正确的应该使用readonly字段
    * 2）方法：
        * 属性：简化语法调用的一种特殊方法，一般用来操作私有字段，便于封装
            * 无参属性（平时说的属性）
                * get，set访问器（只有其中之一可控制字段可读可写，各自均可添加访问修饰符限制），C#编译器编译后实际生成了4个对应的方法
                ```csharp
                private string name;
                public string Name
                {
                    get{return name;}
                    set{name=value}/*value代表总是新值*/
                }
                /*自动实现的属性，无需显式的定义字段，编译器会自动定义*/
                public string Name{get;set;};/*get，set后面的;相当于括号{}*/
                ```
            * 有参属性（索引器）
                * 详见10.2
        * 构造器
            * 实例构造器：将类型的实例初始化为指定状态的特殊方法，方法名与类型名相同
                * 类
                    * 构造器没有显式重写的所有字段默认为0或null
                    * 构造器不能被继承	
                    * 若类没有显式定义任何构造器，C#编译器默认的定义一个无参构造器，若是抽象类abstract，构造器为protected（给子类构造器调用base()使用），否则为public；若显式定义了构造器，则不会有默认构造器
                    * 一个类型可定义多个实例构造器，必须有不同的签名（参数）
                    * 类的实例构造器在访问基类的任何字段之前，必须先调用基类的构造器，若没有显式的调用，则编译器会默认调用基类构造器（若基类未显式的定义构造器），最终，System.Object的公共无参构造器得到调用
                    * 极少数不调用构造器创建实例（详见运行时反序列化）；不要在构造器中调用虚方法
                    * 若有多个构造器，避免使用字段的内联初始化，因为这样要注意代码的膨胀效应（IL代码中的每个构造器里都会将内联初始化的字段初始化一次），所以此时应该考虑创建单个构造器来初始化这些字段，然后其他构造器用this()调用
                    ```csharp
                    class B : A
                    {
                        private int age;
                        public B():base()/*base()可省略*/
                        {
                            age=1;
                        }
                        public B(int num):this()
                        {
                        }
                    }
                    ```
                * 结构
                    * C#编译器不允许结构显式定义无参构造器，也不会默认的生成值类型构造器
                    * 结构创建实例可以不出用new但必须手动初始化每个成员才可以使用，用new则可以初始化为0或null
            * 类型构造器：设置类型的初始状态的特殊方法，class 和 struct（结构虽然可以定义类型构造器，但有时会失效，因此最好不要用），方法名与类型名相同，必须有static修饰符
                * 编译器默认不会生成类型构造器，自定义也只能定义一个，但是若使用静态字段内联初始化，编译器就会生成类型构造器
                * 类型构造器永远没有参数
                * 默认private(未显示)，不允许显式的加访问修饰符
                * 类型构造器中的代码只能访问类型的静态字段，常规使用就是初始化这些字段
                * 类型构造器不应该调用基类的类型构造器，因为static字段不能继承，没有任何意义
        * 析构方法：
        * 扩展方法：class，不改变原引用类型的基础上，扩展方法（C#只支持扩展普通方法）
            * 扩展方法必须放在静态类里边，静态类无其他要求，只是必须具有文件作用域（即一个源代码文件中namespace里的顶级静态类，不能是类里边嵌套的静态类）
            * 扩展方法必须是静态的可访问的，且第一个参数必须为“this+扩展的类型的变量（表示作为哪个类型的扩展方法），其他参数按需随意，例如 
            ```csharp
            public static class StringExtensions
            {
                public static void Name(this String st,int num)
            }
            ```
            * 扩展方法的第一个参数类型，如果扩展了基类型，则同时也扩展了派生类型
        * 分部方法partial
            * 允许使用类似分部类的功能，一个文件只声明方法，另一个文件包括完整的方法实现
        * 操作符重载方法
            * 例如System.String重载了==和!=
            * 详见8.4
        * 转换操作符方法：将类型从一种类型转换为另一种类型的方法
            * 详见8.5
        * （通用）方法的参数
            * （1）可选参数（参数有默认值，调用方法时可省略参数）
                * 有默认值的参数必须放在没有默认值的参数后面
                * 默认值必须是编译时能确定的常量值
                * 不能用于 ref 和 out 关键字的参数
            * （2）不能用var声明参数，var只能定义隐式局部变量
            * （3）参数传值和传引用
                * 传值（默认）
                    * 值类型：传值的时候是复制一个值类型实例的副本到方法中，调用者本身的实例不受影响
                    * 引用类型：实际传的是地址（指针），所以方法中修改了会影响调用这本身的实例，String类型除外
                * 传引用（传地址）的方式传参（ref，out）
                    * 方法定义时和调用方法时参数都要加关键字
                    * 一般来说只对值类型有意义，引用类型默认传的是地址
                    * 具体区别查看9.3
            * （4）可变数量的参数params
                * 示例
                ```csharp
                public Int32 Add(params Int32[] values)
                {
                    Int32 sum=0;
                    if(values != null)/*判断是否没有参数*/
                    {
                        for (Int32 x=0;x<values.Length;x++)
                        {
                            sum+=values[x];
                        }
                    }
                    return sum;
                }
                /*调用方法*/
                Int32 sum = a.Add(1,2,3);
                ```
                * 可变数量参数对性能有影响，除非必要，数组要在堆上分配内存，自动回收垃圾，若参数数量不多可以考虑重载多个参数的方法，或者使用可选参数赋默认值
                * 参数和返回类型设计规范：参数最好用接口，扩大方法使用范围，返回类型最好使用强类型，详见9.5
    * 3）事件：详见委托

## 泛型
* 1.泛型的主要作用：定义泛型的引用类型和值类型，精简代码和避免装箱拆箱
    * FCL中的泛型:System.Collection.Generic 泛型集合命名空间
    * 泛型优势（List<T>和ArrayList）
* 2.泛型基础结构
    * 开放类型List<T>（具有泛型类型参数的类）和封闭类型List<string>（泛型类型实参已经指定）
        * 是不同的类型，在内存中创建的是不同的类型对象
        * 本质是指定泛型类型的实参时创建的封闭类型，（相当于继承自）开放类型
    * 简化泛型类型的写法
        * using StringList = System.Collections.Generic.List<string>; =后面部分必须使用完整类型名
        * 利用var（隐式类型局部变量），让编译器推断类型
    * 代码爆炸
* 3.泛型接口
* 4.泛型委托
    * 委托和接口的逆变协变泛型类型实参（in out）
* 5.泛型方法（可以放在任意类中，其他类型成员均不可单独用泛型，但可以在泛型类中使用类的泛型）
    ```csharp
    public static void swap<T>(ref T o1,ref T o2) 
    {
        T temp = o1;
        o1 = o2;
        o2 = temp;
    }
    /*调用时：
        Generic.swap<string>(ref a,ref b);
        Generic.swap(ref a,ref b);泛型类型实参可以省略，编译器可以自动推断
    */
    ```
* 6.泛型约束
```csharp
public class Generic<T> where T : System.Collections.IList /*T必须是实现IList接口的类型*/
{

}
```

## 接口和委托
* 1.接口（CLR不支持多继承，接口则是“缩水版多继承”）
    * 基本特性：
        * 接口只是定义了方法，不提供具体实现，而继承接口的类必须显示的实现方法，相当于
            * 接口 = 做什么，但不指定你怎么做，属于高级领导，光说话不干事
            * 类实现 = 完成接口规定的任务，属于具体办事的，但必须完成领导安排的
        * 接口不能实例化，是一种特殊的引用类型
        * 值类型可以实现接口，但必须进行装箱
        * 凡是使用【基类型实例】的地方，都能使用【派生类型实例】
        * 凡是使用【具名接口类型】的地方，都能使用【实现接口的一个类型实例】
        ```csharp
        Animal a = new Dog();
        IList<string> = new List<string>();
        ```
    * 定义和实现接口（约定接口名以I开头）
    ``` csharp
    public interface IAnimal /*接口类型的修饰符只能是public，可以省略*/
    {
        /*接口成员只可以有 方法（包括一般方法和属性），事件；
        接口成员访问修饰符必须是public（可以省略），不可以有修饰符static,继承修饰符（abstract，virtual和override）；
        接口成员不能有具体实现，只能定义；
        */
        void Do(string dosomething);
    }
    class Dog : IAnimal
    {
        /*类型实现的接口成员必须是显示的标记为public；
        若不显示的标记 virtual，编译器会将其标记为virtual 和 sealed不让派生类继承和重写此成员；
        若显示的标记 virtual，编译器只会将其标记为virtual，允许派生类重写
        */
        public void Do(string dosomething);
    }
    ```
    * 泛型接口
        * 类型安全，相对于非泛型接口的Object参数
        * 处理值类型装箱次数会变少
        * 类可以实现一个泛型接口若干次，例如同时实现:IList<string>,IList<int>
    * 实现多个具有相同方法名和签名的接口：须在实现的类中显示的实现出来，如 void IList.Dosomething(){}
    * 显示实现接口的利弊（EIMI）详见13.9，13.10
    * 设计：基类还是接口
        * IS-A关系：属于，应该用基类
        * CAN-DO关系：能做某事，应该用接口
* 2.委托（.NET Framework通过委托来提供回调函数机制，委托确保类型安全）
    * 入门参考（http://www.tracefact.net/CSharp-Programming/Delegates-and-Events-in-CSharp.aspx）
    * 简单示例
    ```csharp
    /*委托类型相当于是定义一个代表（一组参数和返回值相同的方法）类型，可作为参数传递给任何一个方法，委托类型代表的是具有规定参数和返回值的方法；注：方法签名由方法名称和一个参数列表(方法的参数顺序和类型)组成*/
    using System;
    using System.Collections.Generic;
    using System.Text;
    namespace Delegate {
        /*定义委托，它定义了可以代表的方法的类型（参数和返回值），dlegate相当于class，GreetingDelegate是一个委托类型*/
        public delegate void GreetingDelegate(string name);
        class Program {
            private static void EnglishGreeting(string name) {
                Console.WriteLine("Morning, " + name);
            }
            private static void ChineseGreeting(string name) {
                Console.WriteLine("早上好, " + name);
            }
            /*注意此方法，它接受一个GreetingDelegate委托类型的方法作为参数*/
            private static void GreetPeople(string name, GreetingDelegate MakeGreeting) {
                MakeGreeting(name);
            }
            static void Main(string[] args) {
                /*调用以委托类型作为参数的方法，传递的实参可以是满足委托类型的任何方法（即参数和返回值相同）*/
                GreetPeople("Jimmy Zhang", EnglishGreeting);
                GreetPeople("张子阳", ChineseGreeting);
                Console.ReadKey();
            }
        }
    }
    输出如下：
    Morning, Jimmy Zhang
    早上好, 张子阳
    ```
    * 委托优势：委托是一种类型，它定义了方法的类型，使得可以将方法当作另一个方法的参数来进行传递，这种将方法动态地赋给参数的做法，可以避免在程序中大量使用If-Else(Switch)语句，同时使得程序具有更好的可扩展性
    * 委托类型可以实例化后，绑定若干具有相同返回值和参数的方法，相当于这些方法的指针
    ```csharp
    /*声明委托，分别赋值*/
    GreetingDelegate delegate1, delegate2;
    delegate1 = EnglishGreeting;
    delegate2 = ChineseGreeting;
    GreetPeople("Jimmy Zhang", delegate1);
    GreetPeople("张子阳", delegate2);
    /*声明委托，一个委托实例可以绑定多个方法，有顺序*/
    GreetingDelegate delegate1;
    delegate1 = EnglishGreeting; /*先给委托类型的变量赋值*/
    delegate1 += ChineseGreeting; /*给此委托变量再绑定一个方法*/
    GreetPeople("Jimmy Zhang", delegate1); 
    delegate1 = EnglishGreeting; /*先给委托类型的变量赋值*/
    delegate1 += ChineseGreeting; /*给此委托变量再绑定一个方法*/
    delegate1 ("Jimmy Zhang");/*将先后调用 EnglishGreeting 与 ChineseGreeting 方法*/
    ```
    * 委托类型实例化后既可以绑定方法也可以取消绑定 delegate1 -= EnglishGreeting;
    * 事件：Event，定义委托类型实例化后的变量，是类型成员
        * 一般使用事件方法：
            * 在类的内部：给事件注册方法可以用"="和"+="，注销方法用"-="
            * 在类的外部：给事件注册方法只能用"+="，注销方法用"-="
            * 触发事件：事件名(传入实参);
            * 示例：
            ```csharp
            MakeGreet += Do;/*给事件注册方法：Do是方法名*/ 
            MakeGreet("123");/*触发事件，会调用执行所有注册在此事件上的方法*/
            ```
        * C#使用事件标准：
```csharp
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
namespace Mail
{
    /*需要 传递附加信息 给 已注册事件的方法 时，建立新类（继承自EventArgs）容纳信息；
    若不需要传递附加信息，则可以直接使用 EventArgs.Empty
    */
    class NewMailEventArgs : EventArgs
    {
        private readonly string m_from, m_to, m_subject;
        public string From { get { return m_from; } }
        public string To { get { return m_to; } }
        public string Subject { get { return m_subject; } }

        public NewMailEventArgs(string from, string to, string subject) 
        {
            m_from = from;
            m_to = to;
            m_subject = subject;
        }

    }
    class MailManager
    {
        /*
            泛型委托EventHandler<T>，定义如下：
            public delegate void EventHandler<TEventArgs>(object sender, TEventArgs e);
            所以方法原型必须符合 返回值void，参数为object sender和TEventArgs e，即满足：
            void MethodName(object sender, TEventArgs e){};
        */
        /*定义事件成员*/
        public event EventHandler<NewMailEventArgs> NewMail;
        /*定义引发事件方法*/
        protected virtual void OnNewMail(NewMailEventArgs e) 
        {
            /*出于委托线程安全，不直接使用事件NewMail
            EventHandler<NewMailEventArgs> temp = Volatile.Read(ref NewMail);
            if (temp != null)
                temp(this, e);*/
            e.Raise<NewMailEventArgs>(this, ref NewMail);
        }
        /*定义方法将输入转化为期望事件*/
        public void SimulateNewMail(String from,string to,string subject)
        {
            NewMailEventArgs e = new NewMailEventArgs(from,to,subject );
            OnNewMail(e);
        }

    }

    /*扩展委托线程安全*/
    public static class EventArgExtensions
    {
        public static void Raise<TEventArgs>(this TEventArgs e, Object sender, ref EventHandler<TEventArgs> eventDelegate) 
        {
            /*出于委托线程安全，不直接使用事件NewMail*/
            EventHandler<TEventArgs> temp = Volatile.Read(ref eventDelegate);
            if (temp != null)
                temp(sender, e);
        }
    }
    /*给事件注册方法*/
    class Fax
    {
        public Fax(MailManager mm)
        {
            mm.NewMail += FaxMsg;
        }
        private void FaxMsg(object sender, NewMailEventArgs e)
        {
            Console.WriteLine("Faxing mail message:");
            Console.WriteLine("From={0},To={1},Subject={2}", e.From, e.To, e.Subject);
        }
        public void Unregister(MailManager mm)
        {
            mm.NewMail -= FaxMsg;
        }
    }
    /*触发事件*/
    class Program
    {
        static  void Subject(object sender,NewMailEventArgs e)
        {
            Console.WriteLine(sender);
            Console.WriteLine(e.From +"\n"+e.To +"\n"+e.Subject);
        }
        static void Main(string[] args)
        {
            MailManager manager = new MailManager();
            manager.NewMail += Subject;
            Fax s = new Fax(manager );
            s.Unregister(manager);
            manager.SimulateNewMail("icql@qq.com","37612949@qq.com","test");
            Console.ReadKey();
        }
    }
}
```
* 3.委托原理，lambda表达式演变
    * 多播委托调用的方法得到的值是最后一个方法的返回值
    * 委托内部构造
        * new一个委托对象，在内存中抽象有三部分：_methodPtr（方法指针：指向当前委托实例指向的方法在内存中的地址），_target（目标对象：绑定到委托实例的方法的所有者，实例方法是实例对象，静态方法是类对象），委托链（实际类似是一个委托数组，用来绑定一系列委托实例，多播委托实例是多个委托实例，委托链用来在执行委托时按照顺序执行）
    * 泛型委托 Func<T1,T2,T3...TResult>;Action<T1,T2...> Func有返回值（必须得有参数），Action无返回值（可以没有参数）
    * lambda表达式演变过程
    ```
        \\匿名方法 
        Func<int,int,int> funcDemo = delegate(int a, int b){return a + b;};
        \\lambda语句
        Func<int,int,int> funcDemo = (int a, int b) => {return a + b;};
        \\lambda表达式
        Func<int,int,int> funcDemo = (int a, int b) => a + b;
        Func<int,int,int> funcDemo = (a,b) => a + b;

        无参数lambda () => 语句;


## 字符和字符串
* 1.字符 System.Char（值类型）
    * .NET Framework中，1个字符占2个字节16位，总是被表示成Unicode代码值
    * 可以与Int32类型互转
* 2.字符串 System.String（引用类型）
    * 一个String代表一个不可变的顺序字符集（immutable），存储在GC堆上
    * 1）构造字符串
        * 默认只能使用简化的 string str = "123456";构造，不允许使用new来构造（不安全代码可以利用Char*或Sbyte*构造）
        * string str = "HI" + " aaa";会在GC堆上创建多个字符串对象，对性能有影响，因此应该使用System.Text.StringBuilder类型
        * 逐字字符串@，可以将""间的所有字符视为字面值而非转义字符，可以用来跨行，推荐使用
    * 2）字符串是不可变的：字符串一经创建则不可变，其中的字符串方法操作后返回的是新的字符串
    * 3）字符串池
* 3.高效率构造字符串System.Text.StringBuilder
* 4.字符串的其他操作
    * String.Format("{0}",str);
    * Tostring()
    * 字符串解析为对象Parse()方法，提供解析字符串的类型都有此方法
* 5.编码解码
    * UTF-8，UTF-16
    * System.Text.Encoding，System.Text.Decoder

## 枚举类型和数组
* 1.枚举类型enum（值类型）
```csharp
/*枚举类型不能定义任何方法（包括属性），事件*/
/*枚举类型相当于是常量值，编译时会直接把枚举符号替换为数值*/
public enum Color
{
    Write=1,Red,Green,Blue=1,Orange
}
/*枚举值只能是int类型，不赋值时默认值是0，1，2，3...*/
Color c = Color.Blue;/*实例化*/
int i = (int)c;
```
* 2.数组（引用类型）
    * 分类
        * 值类型数组-数组元素为值类型，创建在GC堆上，内部是实际的值数据
        * 引用类型数组-数组元素为引用类型，创建在GC堆上，内部是引用地址
    * 初始化数组元素
        * 传统，先声明后逐个赋值
        * 数组初始化器 String[] names = new String[]{"AA","BB"};
        * 不定项数组 String[] names={"1","2"};
    * 数组转型：只需满足继承关系且维数相同 Object[,] fa=new File[5,10];
    * 所有数组隐式派生于 System.Array，实现IEnumerable，ICollection和IList（非泛型接口，将数组所有元素视为object）
    * 数组作为参数传递符合引用类型的传递（实际传递的是地址），所以方法中要避免改变原数组，须在方法中定义新数组

## 定制特性和可空值类型
* 1.定制特性：将一些附加信息与某个特定的目标元素关联起来的方式
    * 使用特性：
        * 定制特性类必须继承System.Attribute，特性实际是一个类型的实例
        * 应用特性时，编译器允许省略Attribute后缀
        * 一个元素可以应用多个特性 [Serializable][Flags]，或者[Serializable,Flags]
    * 定制特性：
    ```csharp
    /*定制特性：必须继承System.Attribute，最好以Attribute结尾，必须至少要有一个公共构造器
    定制的特性类必须应用System.AttributeUsageAttribute类的实例
    此特性类有两个默认构造器，第1个参数的是表明特性应用的类型种类，后面的参数用来设置属性值，
    AllowMultiple属性用来设置是否可以对选定的元素多次应用该特性，InHerited属性用来设置特性应用基类时，是否应用它的派生类和重写方法
    */
    [AttributeUsage(AttributeTargets.Class,Inherited=false,AllowMultiple=true)]
    public class FlagsAttribute : System.Attribute
    {
        public FlagsAttribute() { }
    }
    ```
* 2.可空值类型
    * 背景：值类型无法表示为null，但数据库和别的语言相同类型可能会是null，所以需要可空值类型来满足
    * 解决：System.Nullable<T>结构，T被约束为只能是值类型，可空int类型可以表示为 Nullable<Int32>，C#中可以简写 Int32? x = 5;（在值类型后紧跟一个?就可以表示该类型可以为空）
    * 操作符：包含null时的处理
    * 空结合操作符 ??：若??左边的操作数不为null则返回原来的值，若为null则返回??右边的值
    * 可空值类型的装箱拆箱：
        * 装箱：若为null，CLR将直接返回null，若不为null，则在可空值类型实例中取出值，装箱成普通的已装箱值类型
        * 拆箱：若已装箱的值类型为null，则拆箱为Nullable<T>类型，CLR还会将其值设为null
    * 通过可空值类型调用GetType()返回的是 Nullable<T> 的T类型而不是Nullable<T>
    * 可空值类型默认可以使用原类型的接口（虽然内部并没有实现该接口）

## 异常
* 1.异常：是指使用某部分代码没有完成期望得到的结果
* 2.异常处理机制：DOTNET使用的是Micorsoft Windows提供的结构化异常处理机制（SEH）
    * try块：一个try块至少要关联一个catch和finally块，需要执行一般性的资源清理操作或者，需要从异常中恢复，就可以放到try块中，负责清理的代码放到finally块中
    * catch块：catch块包含的是响应一个异常需要执行的代码，一个try代码块可以关联0个或多个catch块，如果try块中的代码没有造成异常，CLR永远不会执行所有catch块，线程直接跳过catch块直接执行finally块

## clr编译执行源代码过程
* 1.基本编译过程：C#源代码-C#编译器-程序集文件(一个或多个托管模块)
    * 详述：C#编译器编译源代码，源代码中存在其他类型，则必须引用对应的程序集；编译命令举例csc.exe /out:Program.exe /t:exe /r:MSCorLib.dll Program.cs，参数：/out输出文件名，/t生成格式，Console User Interface,CUI使用/t:exe，Graphical User Interface,GUI使用/t:winexe，Windows Store应用使用/t:appcontainerexe，dll文件使用/t:library，/r引用程序集，其中MSCorLib.dll文件是特殊文件，基本包含了所有核心类型，默认引用；.rsp响应文件(包含一些命令选项，具体有加载的系统程序集等)，代替每次命令输入的重复参数，例MyProject.rsp文件，使用时csc.exe @MyProject.rsp CodeFile1.cs CodeFile2.cs，编译器会在编译器当前所有目录下查找全局CSC.rsp响应文件(引用了很多常用程序集)，添加编译器默认参数，当参数冲突时，以本地rsp为主；C#编译器和AL.exe可以将资源文件整合进单独的dll中，也可使用AL.exe将资源文件放置在并列逻辑位置形成普通多文件程序集；编译多文件程序集和单文件程序集或单独不包含清单元数据表托管模块的其他诸多命令参数不再此详述

* 2.基本执行过程(即时编译，边编译边执行)：
    * win32和win64平台简述
        * VS编译器默认platform平台选项是anycpu，还可以选择生成x86，x64，编译器将platform信息保存在托管模块的PE32或PE32+头中
        * PE32文件：可以运行在x86和x64，win64版本通过WoW64(Windows on Windows64)技术运行PE32文件
        * PE32+文件：只能运行在x64
        * MSCorEE.dll 文件位置：win32系统的x86版本在System32/目录中；win64系统的x86版本在SysWow64/目录中，x64版本则在System32/目录中(为了向后兼容)(http://blog.csdn.net/smking/article/details/4484078)
    * 应用程序在windows上运行过程：启动命令创建进程-windows检查程序集(exe或dll)文件中主托管模块的PE文件头-选择对应版本(32位或64位)的MSCorEE.dll加载-进程的主线程调用MSCorEE.dll的一个方法(_CorExeMain方法，对于dll是_CorDllMain方法)-用此方法初始化CLR并且加载EXE程序集，再调用EXE的IL代码入口方法(Main)启动托管应用程序并运行
    * CLR中JIT(just-in-time即时)编译器：将IL语言编译成本机CPU指令(native code)，本质是一个内置方法（在MSCorEE.dll中）
        * 编译执行过程：
            * JIT编译器在负责实现类型（Console）的程序集的元数据中查找被调用的方法（WriteLine）
            * 从元数据定义表中获取该方法的IL
            * 分配内存块（在C#内存区域的代码区）
            * 将方法的IL编译成native code，然后将其存储到上述的内存中
                * 编译方法前，CLR检测该方法中的代码引用的所有类型，利用加载的程序集提取有关数据在 堆 中创建存储相应的类型对象
                * 编译方法时，每个方法都包括：序幕代码（prologue）和尾声代码（epologue）
                    * 序幕代码负责初始化变量创建对象，先后将方法的参数变量地址，方法的返回地址，方法内部的局部变量值类型压入栈，局部变量引用类型则在GC堆中开辟内存，栈是高位——低位，先进后出
                    * 尾声代码负责方法做完后对其清理，将CPU指令指针设为方法返回地址返回至调用者
            * 在上面存储的类型对象的方法表中修改与方法对应的记录项，使它指向为该方法分配的内存位置
            * 跳转到内存块中的本机代码，执行
            * 第二次遇到需要执行该方法时，将会直接跳转到内存块中执行编译过后的native code
    * NGen.exe：将IL代码提前生成native code，避免JIT即使编译，提高启动速度；但是会有诸多限制，执行环境与NGen.exe编译过后的文件特征不一致就要重新使用JIT编译运行，而且NGen.exe不会像JIT对代码进行优化

## 内存管理和垃圾回收
* 1.内存管理（虚拟进程空间，并非物理内存）
    * （1）内存区域（http://blog.csdn.net/lerit/article/details/4441239）
        * 1）代码区
            * JIT编译后的方法的native code
        * 2）数据区	
            * 栈区（stack）：由系统自动分配释放，程序开始运行创建线程时会在内存中分配一个1M大小左右的栈区(所以又叫线程栈)，多线程每个线程会创建自己单独的栈
                * 存储的数据包括：值类型实例，引用类型的地址，方法的返回地址等
                * 栈从高位地址向低位地址构建（高——低），栈存储释放遵从“先进后出”的原则
            * GC托管堆区（manage heap）：托管语言是由CLR的GC托管分配释放（所以又叫托管堆）
                * 存储的数据包括：引用类型的实际数据
                * LOH堆区（large object heap）:类型的实例的Size>=85000byte，存储在此区域
            * 字符串常量池

    * （2）对象，类型对象		
        * CLR要求所有对象用 new 操作符创建
            * 值类型：new 操作符所做的事情：
                * 1）直接将实例存储在栈上，并且初始化 字段为0
            * 引用类型 new 操作符所做的事情：
                * 1）计算对象所需字节数，包括该类型及其基类型定义的所有实例字段所需的字节数和类型对象指针、同步块索引所需字节数，类型指针和同步块索引是CLR用来管理对象的
                * 2）在托管堆上分配该对象所需内存空间
                * 3）初始化类型对象指针和同步块索引
                * 4）执行构造函数。大多数编译器都在构造函数中自动生成一段代码调用基类构造函数，每个类型的构造函数在执行时都会初始化该类型定义的实例字段(若构造函数未初始化，默认字段为引用类型初始化为null，值类型为0)
                * 5）返回指向新建对象的一个引用，保存在对象变量中
        * 引用类型的对象
            * 存储在GC托管堆上（只存储字段，不存储方法）
            * 包括：两个开销字段（类型对象指针，同步索引块，32位应用程序各自需要32bit，64位各自需要64bit，分别是8个字节和16个字节），（以对应的类型对象为模板生成的）实例字段（值类型字段存储在这里，引用类型字段相当于调用构造器方法创建了一个新的对象存储在托管堆）
            * 类型对象指针指向对应的类型对象内存地址
        * 类型对象
            * 存储在堆上
            * 包括：两个开销字段（类型对象指针，同步索引块），类型字段（静态字段），一个指向基类型的字段，方法表（只存储各个方法名和调用方法的地址）
            * 类型对象指针指向默认的MSCorLib.dll中定义的System.Type类型创建的一个特殊的类型对象，本类型对象是这个类型对象的“实例”，System.Type类型对象也是一个类型对象，它的类型对象指针指向它自己

* 2.垃圾回收
    * 托管堆分配资源
        * 进程初始化时，CLR会划出一个地址空间区域作为GC托管堆，CLR还要维护一个指针NextObjPtr，指向下一个对象在堆中分配的位置，初始NextObjPtr设为该地址空间区域的基地址
        * 分配对象前CLR检查区域是否有分配对象所需的字节数
            * 若足够，就在NextObjPtr指针指向的地址存入该对象，调用构造器初始化，NextObjPtr指针会加上该对象所占的字节数得到一个新的地址，作为下一个对象存储的地址，new操作符将返回该对象的地址引用
            * 若不够，则启动垃圾回收，回收用不到的对象，当这个区域被非垃圾对象填满后，CLR会分配更多的区域，这个过程一直重复，直到进程地址空间被填满，32位进程最多分配1.5GB，64位进程最多分配8TB
    * 垃圾回收算法：GC（garbage collection）
        * GC暂停所有线程
        * GC标记：遍历所有对象，检查方法的参数变量和局部变量，标记同步索引块字段的一位（0没有变量引用，1有变量引用）
        * GC回收删除：所有标记为0的对象
        * GC压缩：将所有幸存下来的对象转移整合，使其占用连续的内存地址空间（GC原生解决堆空间碎片化问题）
        * 托管堆的NextObjPtr指针指向最后一个幸存者之后的位置
    * 垃圾回收机制（详见21.2节）
        * CLR的GC是基于代的垃圾回收器，0代，1代和2代
            * CLR初始化时会为第0代对象选择一个预算容量（以KB为单位），之后会动态调整0，1，2代的预算
            * 0代永远是新创建对象的存储区，1代是GC回收0代压缩后的对象存储区，2代是GC回收1代压缩后的对象存储区
            * 0代预算满了，GC进行垃圾回收，若1代语段没满，则GC不检查1代垃圾，若1代预算也满了，则GC对1代进行垃圾回收，以此类推
    * 垃圾回收模式（2种）
        * 工作站模式：针对客户端应用程序，GC造成的延迟很低，线程挂起的时间很短
        * 服务器模式：针对服务器端应用程序优化，默认是以工作站模式运行，要在配置文件添加gcConcurrent元素才可以开启服务器模式（详见21.2.3节）
    * 需要使用特殊清理的类型
        * 使用本机资源的类型需要特殊清理（例如读写文件，连接数据库等）
        * 对象变为不可访问后，将自动调用System.Object的Finalize方法终结，之后再垃圾回收
        * 析构函数（对象变为不可访问后，若有析构函数，则默认调用析构函数，再调用终结器http://blog.csdn.net/tyb1222/article/details/8871842）
        ```csharp
        class Car  
        {  
            /*析构函数
                不能在结构中定义析构函数。只能对类使用析构函数
                一个类只能有一个析构函数
                无法继承或重载析构函数
                无法调用析构函数。它们是被自动调用的
                析构函数既没有修饰符，也没有参数
            */
            ~Car()
            {  
                /* cleanup statements...*/       
            }  
        } 
        /*默认调用析构函数时，实际执行的代码如下*/
        protected override void Finalize()    
        {    
            try  
            {  
                /*Cleanup statements...*/ 
            }       
            finally   
            {  
                base.Finalize();    
            }  
        }   
        ```
        * 自定义使用本机资源的类型: FCL提供了System.Runtime.InteropServices.SafeHandle抽象类，用于我们自己创建封装使用了本机资源的类来继承，建议使用（详见21.3.1节）
        * 使用包装了本机资源的类型（一般都实现了IDisposable接口）
            * 例如System.IO.FileStream
            * 调用Dispose方法只是清理使用的本机资源，内存还没有回收
            ```csharp
            Byte[] bytesToWrite = new Byte[] { 1,2,3,4,5};
            /*
            //创建临时文件
            FileStream fs = new FileStream("Temp.dat", FileMode.Create);
            try
            {
                fs.Write(bytesToWrite,0,bytesToWrite.Length);
            }
            finally
            {
                if (fs != null)
                    fs.Dispose();
            }
            */
            /*上述的简写*/
            using (FileStream fs = new FileStream("Temp.dat", FileMode.Create))
            {
                fs.Write(bytesToWrite, 0, bytesToWrite.Length);
                /*省略了 fs.Dispose();*/
            }

            File.Delete("Temp.dat");
            ```

## appdomain-反射-序列化
## 线程基础
* 1）进程process：一个进程中运行着应用程序的每个实例，进程实际是应用程序的实例要使用的资源的集合，每个进程都被赋予一个虚拟的地址空间，多个进程之间相互隔离无法访问
* 2）线程thread：线程的职责是对CPU进行虚拟化，windows为每个进程都提供了该进程专用的线程，功能相当于一个CPU（逻辑CPU）
    * 线程开销：内存空间开销和响应时间开销
        *，线程包含以下要素：
        * 线程内核对象
        * 线程环境块
        * 用户模式栈
        * 内核模式栈
        * DLL线程连接和线程分离通知
    * windows任何时刻只能将一个线程分配给一个CPU，每个线程只能占用
* 3）
    ```
        Thread t = new Thread(delegate () 
        {
            while (true)
            {
                Console.WriteLine(DateTime.Now.ToString());
                Thread.Sleep(2000);
            }
        });
        t.IsBackground = true;
        //仅仅是告诉操作系统，该线程已经准备好了，实际什么时候执行，看操作系统的根据自己的优先级去执行
        t.Start();
    ```
    * 线程默认是前台线程，一个进程退出标志是所有的前台线程结束后才会退出
    * CLR启动一个应用程序执行 Main方法的 是主线程（前台线程），
    * 多线程执行是通过 CPU调度单个去执行每个线程，同一时间CPU只能去执行一个线程
    * 一个应用程序使用多线程之所以会快，是因为，线程数多了，在操作系统中的所有线程中占比增高，得到CPU调度的几率增大，所以会快
    * 除了主线程，自己创建的线程尽量使用后台线程

## 流，编码，xml，序列化和反序列化
* 1.流
    * 理解：类似于一个载体，将数据分解为特定大小的比特或字节等等为单元，用来传输数据，流的传输有两个方向，对应着输入流和输出流
    * flush
    * 网络流，内存流，文件流
* 2.编码（System.Text.Encoding）
    * 码表
        * ASCII 码表：用1个字节表示，2^7=128种符号，最高位是符号位，只有英文字母，英文符号，阿拉伯数字等
        * GB2312 码表：兼容ASCII码表；英文1个字节，中文2个字节，判断符号位为+则是英文字节，第1个字节符号位为-第2个字节符号位为-则这2个字节加起来是中文字符；(2^7-1)*(2^7-1)=16129种符号
        * GBK 码表：兼容GB2312码表，英文1个字节，中文2个字节；判断符号位为+则是英文字节，第1个字节符号位为-第2个字节符号位不限则这2个字节加起来是中文字符；(2^7-1)*(2^8)=32512种符号
        * Big5 码表：台湾
        * Unicode 国际码表；中英文全2个字节
        * UTF8 国际码表；英文1个，中文3个字节
    * 字节数组和字符串互转(https://www.cnblogs.com/Maxq/p/5953682.html)
* 3.文件操作（命名空间System.IO）
    * 路径：Path类（操作路径字符串）
    * 驱动器:DriveInfo类（实例化类，与指定驱动器关联）
        * 有一个GetDrivers()静态方法
    * 目录
        * Directory类（静态类）
            * CreateDirectory()创建目录
            * Delete()删除目录
            * Move()移动目录
            * GetDirectories()获取指定目录下的所有子目录
            * GetFiles()获取指定目录下的所有文件
            * Exist()指定目录是否存在
            * GetParent()获取父目录
        * DirectoryInfo类（实例化类，与指定目录关联）
            * 方法
                * Create()
                * Delete()
                * MoveTo()
                * CreateSubdirectory()在指定目录创建一个或多个子目录
                * GetFiles()
                * GetDirectories()
            * 属性
                * Atrributes诸多信息
                * CreationTime
                * FullName和Name目录名
                * Parent父目录
                * Root获取指定目录根目录
    * 文件
        * File类（静态类）
            * Open()
            * Create()
            * Delete()
            * Copy()
            * Move()
            * Exists()
            * SetAttributes()
        * FileInfo类（实例类，关联文件）
            * 诸多方法属性
    * 读写文件
        * FileStream类（文件流：字节流）
            * 读写文件中的数据
            * 创建FileStream实例有多种方法：上述File和FileInfo类的方法，或者直接使用其构造方法FileStream(string path,FileMode mode,FileAccess access)
            * 方法
                * Read()用byte[]数组接收
                * Write()用byte[]数组写入
                * Seek()将流中指针设置为指定位置，不使用此方法时默认从流的开始位置到结尾位置
        * StreamRead和StreamWrite类（文本文件的读写流）
        * 大文件操作，先利用流加载固定长度字节数组，依次操作
* 4.XML数据
    * 语法规范
    * 读写XML文件
        * Dom（XmlDocument：Net2.0（使用Xpath）、XDocument:Net3.5以上(Linq)）：文档较大，一次加载在内存中，会内存溢出
        * Sax（事件驱动，.net中使用XmlReader和XmlWriter代替（流））
        * XmlSerializer序列化，需要先定义类
        * Linq To XML：就是XDocument（使用lambda表达式）
    * XmlDocument使用
        * 在内存中创建一个Dom对象
* 5.json数据
* 6.序列化和反序列化

## ado.net
``` bash
* 1.ADO.NET组成
    * 数据提供程序（常用类）
        * Connection, 用来 连接 数据库
        * Command, 用来执行 SQL语句
        * DataReader, 只读、只进的结果集,一条一条读取数据（StreamReader、XmlReader类库中这些Reader的使用方式差不多）
        * DataAdapter, 一个封装了上面3个对象的对象
    * 执行方法
        [ExecuteNonQuery、ExecuteScalar和ExecuteReader(Reader的特性)](http://blog.csdn.net/gengyudan/article/details/11890319)
    * 数据集（DataSet）,临时数据库，断开式数据操作

* 2.原始操作数据库过程：
    string conStr = "Data Source=A6924;Initial Catalog=TEST;User Id=sa;Password=006924";/*创建连接字符串*/
    using (SqlConnection con = new SqlConnection(conStr))/*创建连接对象*/
    {
        string sql = "";/*sql语句*/
        using (SqlCommand cmd = new SqlCommand(sql, con))/*创建Command对象*/
        {
            con.Open();/*打开数据库*/
            /*不返回数据*/
            cmd.ExecuteNoQuery();
            /*返回单个数据*/
            string result=cmd.ExecuteScalar();
            /*返回多行多列*/
            using (SqlDataReader reader = cmd.ExecuteReader())/*创建reader对象,执行操作*/
            {
                if (reader.HasRows)/*判断是否查询到数据*/
                {
                    while (reader.Read())
                    {
                    string parm = reader.GetString(0);
                    ...
                    }
                }
            }
        }
    }

* 3.参数化sql语句
    * T-SQL语句中，参数用 @参数名 代替
        select count(1) from 表名 where loginId=@loginId and loginPwd=@loginPwd
    * 给参数赋值(2种)
    /*推荐使用*/
    SqlParameter paramLoginId = new SqlParameter("@loginId",SqlDbType.Varchar,50){Value=值}
    SqlParameter[] pms=new SqlParameter[]
    {
        new SqlParameter("@loginId",SqlDbType.Varchar,50){Value=值},
        new SqlParameter("@loginPwd",SqlDbType.Varchar,50){Value=值}
    }
    cmd.Parameters.Add(paramLoginId);
    cmd.Parameters.AddRange(pms);
    /*直接使用*/
    cmd.Parameters.AddWithValue("@loginId",值)

** 4.连接池

* 5.向数据库中插入null值：不能直接使用 C# 中的 null,必须使用 DBNull.Value

* 6.DataAdapter：DataAdapter是一个封装了3个（Connection,Command,DataReader）的对象，使用时只需2个参数(sql语句,连接字符串conStr)
    string conStr = "Data Source=A6924;Initial Catalog=TEST;User Id=sa;Password=006924";
    string sql = "select * from NE_CARD_FULLCARDINFO";
    DataTable dt = new DataTable();
    using (SqlDataAdapter adapter = new SqlDataAdapter(sql,conStr)) 
    {
        adapter.Fill(dt);
    }
    this.dataGridView1.DataSource = dt;

* 7.DataSet和DataTable
    * DataSet：临时数据库，存储在内存的数据库
    * DataTable：DataSet中表

* 8.封装sqlHelper
    * 1.完整封装SqlHelper
    [SqlHelper.cs](https://github.com/chenqinglin93/File-backup/blob/master/App_Code/SqlHelper.cs)
    [OracleHelper](https://github.com/chenqinglin93/File-backup/blob/master/App_Code/OracleHelper.cs)

    * 2.简单封装SqlHelper
    添加引用System.Configuration
    using System.Data.SqlClient;
    using System.Configuration;


    public static class SqlHelper
    {
    /*定义连接字符串,
    readonly修饰的变量，只能在初始化时赋值，以及在构造函数装赋值，其他地方只读,
    增加配置文件
    <connectionStrings>
        <add name="NewEnergyConnectionString" connectionString="Data Source=A6924;Initial Catalog=TEST;User Id=sa;Password=006924" providerName="System.Data.OracleClient"/>
    </connectionStrings>
    */
    private static readonly string conStr = ConfigurationManager.ConnectionStrings["NewEnergyConnectionString"].ConnectionString;
    /*1.执行增(insert)删(delete)改(update)*/
    /*private static int ExecuteNonQuery(string sql,dynamic param = null)*/
    public static int ExecuteNonQuery(string sql, params SqlParameter[] pms)
    {
        using (SqlConnection con = new SqlConnection(conStr))
        {
            using (SqlCommand cmd = new SqlCommand(sql, con))
            {
                if (pms != null)
                {
                    cmd.Parameters.AddRange(pms);
                }
                con.Open();
                return cmd.ExecuteNonQuery();
            }
        }
    }
    /*2.执行查询，返回单个值*/
    public static object ExecuteScalar(string sql, params SqlParameter[] pms)
    {
        using (SqlConnection con = new SqlConnection(conStr))
        {
            using (SqlCommand cmd = new SqlCommand(sql, con))
            {
                if (pms != null)
                {
                    cmd.Parameters.AddRange(pms);
                }
                con.Open();
                return cmd.ExecuteScalar();
            }
        }
    }
    /*3.执行查询，返回多行多列*/
    public static SqlDataReader ExecuteReader(string sql, params SqlParameter[] pms)
    {
        SqlConnection con = new SqlConnection(conStr);
        using (SqlCommand cmd = new SqlCommand(sql, con))
        {
            if (pms != null)
            {
                cmd.Parameters.AddRange(pms);
            }
            try
            {
                con.Open();
                /*System.Data.CommandBehavior.CloseConnection这个枚举参数表示将来使用完SqlDataReader后，在关闭reader的同时，SqlDataReader内部关联的Connection对象也关闭掉*/
                return cmd.ExecuteReader(System.Data.CommandBehavior.CloseConnection);
            }
            catch
            {
                con.Close();
                con.Dispose();
                throw;
            }
        }
    }
    /*4.执行查询，返回DataTable*/
    public static DataTable ExecuteDataTable(string sql, params SqlParameter[] pms)
    {
        DataTable dt = new DataTable();
        using (SqlDataAdapter adapter = new SqlDataAdapter(sql, conStr))
        {
            if (pms != null)
            {
                adapter.SelectCommand.Parameters.AddRange(pms);
            }
            adapter.Fill(dt);
        }
        return dt;
    }
    }
```

## asp.net-mvc

``` html
### Controller控制器

### View视图
* ViewBag、ViewData和ViewDataDictionary
    * ViewDataDictionary是一个特殊的字典类，ViewData是它的实例，ViewBag是ViewData的动态封装器
    * ViewBag.Date等同于ViewData["Date"]，两者的重要区别在于ViewBag是动态的，不具有真正的类型，无法做强类型（主要用作智能感应，方便编写页面代码），不能作为参数传递，而ViewData可以
* 强类型视图：控制器方法的返回值直接作为
* Razor使用规范
    * 单行：@C#代码;
    * 多行：@{C#代码}
    * C#代码与html标签混合，须使用@
    * 当Razor和html有二义性时，使用()区分
    * @@转义@
    * html编码：Razor是html自动编码，是指C#字符串里的html代码若要输出html代码，可以使用@Html.Raw("")，即使用一个System.Web.IHtmlString对象实例
    * javascript编码：@Ajax.JavaScriptEncode(ViewBag.Username) 代替 @ViewBag.Username避免js中注入攻击
    * 服务器端注释 @* *@
* 模板布局视图：@RenderBody()——主体部分（引用该布局的视图在此位置），@RenderSection("Footer")节，多个重载，可设置可选节，引用视图中使用 @section Footer{<h1>内容</h1>}
* ViewStart：解决使用普通视图作为布局页引起的冗余问题，默认新建MVC项目会自动生成_ViewStart.cshtml文件，默认指定一个布局。。。详细待后续学习
* 部分视图PartialView
	
### Model模型
* 使用EF框架，代码优先
    * 在Models文件夹下创建有关联的各个实体类
    * 再添加控制器（选择带有EF框架的选项），选择模型，创建数据访问上下文类
    
### HTML辅助方法
### 注解和验证

### controller向view传递数据
* 1. Model
后台Action方法里
return View(object model);
前台使用
@model 类型   必须在第一行声明
@Modle.属性名

* 2. ViewBag
后台Action方法里
ViewBag.xxx="";
前台
@ViewBag.xxx

* 3. ViewData(现在不常用)
后台Action方法里
ViewData["Message"] = "Hello";  
前台
@ViewData["Message"]  

* 4. 参考
http://www.cnblogs.com/zyh-club/p/4941576.html

### view向controller传递数据
(前台传递的数据名称须和后台Action中的参数名称需要保证一致)
* 1.url传参
    * 1.1 MVC路由机制
        * 1.1.1 /控制器名/方法名/参数名=值
        * 1.1.2 /控制器名/方法名/值	（一个参数时默认复制给参数，无需写参数名）

* 2. 表单Form（无返回值）
    * 2.1 View页面的Form形式
        * 2.1.1 传统Form表单
            <form action="@Url.Action("AskForm")" method="post">
                <input type="text" name="input1" value="默认值" />
                <input type="text" name="input2" value="默认值" />
                <input type="submit" value="提交"/>
            </form>
        * 2.1.2 MVC：HtmlHelper方法
            * @Html.BeginForm()
                @using (Html.BeginForm("Apply", "Star", FormMethod.Post, new  {@class="MyForm"}))
                {
                    <input type="text" name="input" value="默认值" />
                    @Html.TextBox("CARNO")
                    <input type="submit" value="提交"/>
                }
            * @Html.BeginRouteForm(),与上面类似
    * 2.2 传递数据类型及实现方法
        * 2.2.1 传递基本数据：前台html表单元素name属性值无规则，后台Action方法参数(name属性值1,...),或者后台Action方法里 string Username = Request["html表单元素name属性值"];
        * 2.2.2 传递model(一个对象)：前台html表单元素name属性值对应某类对象的属性
            前台
            @using (Html.BeginForm("Index", "Home", FormMethod.Get))
            {
                <td> @Html.TextBox("No") </td>
                <td> @Html.TextBox("Name") </td>
                <td> @Html.TextBox("Gender") </td>
                <td> @Html.TextBox("Address") </td>
                <td><input type="submit" value="提交" /></td>
            }
            后台
            public class YYM
            {
                public string No { get; set; }
                public string Name { get; set; }
                public string Gender { get; set; }
                public string Address { get; set; }
            }
            public ActionResult Index(YYM ca)
            {
                var d = ca.Name;
                var s = ca.No;
                return View();
            }
        * 2.2.3 传递集合：
            * 传递基本类型集合(数组)
                前台：html表单元素name属性值相同
                后台：public ActionResult Index(List<string> html表单元素name属性值)
            * 传递模型集合(暂时不会)

* 3. AJAX和Jquery（有返回值的，异步，网址不变）
    * 3.1 View页面的AJAX形式
        * 3.1.1 MVC：AjaxHelper方法
            * 需要引入：
                <script src="~/Scripts/jquery-1.8.0.min.js"></script>
                <script src="~/Scripts/jquery.unobtrusive-ajax.min.js"></script>
            * @Ajax.BeginForm()
            @using (Ajax.BeginForm("Excute","Home",
                new AjaxOptions
                {
                    OnBegin = "return ValidateLog()提交前验证有返回值的js函数名"
                    UpdateTargetId="成功后更新的html元素ID",
                    OnSuccess = "con，回调js函数名(只写函数名)"
                }))
            {
                @Html.TextBox("name")
                @Html.TextBox("name")
                <input type="submit" value="提交" />
            }
            <script>
                function ValidateLog() {
                    if()*****
                    return false;
                }
                function con(data) {
                    alert(data);
                }
            </script>
            * @Ajax.BeginRouteForm()
            * @Ajax.ActionLink()
        * 3.1.2 js函数提交：
            function()
            {
            绑定要提交的data数据值
            var data= ;
            /*Jquery里AJAX的4种形式:如果提交的数据是js对象，须在data:使用JSON.stringify()将对象转为字符串*/
            $.ajax({
                type: "post",
                contentType: 'application/json;charset=utf-8',
                url: '/Resources/GetList.ashx',
                data: 提交的数据,
                success: function (data) {}
            });
            $.get("url",{提交的数据},function(data){});
            $.post("url",{提交的数据},function(data){});
            $.getJSON("url",{提交的数据},function(data){}); 
            }
    * 3.2 传递数据类型及实现方法
        /*传递基本数据类型*/
        前台js:
        var obj = {
                    parm1: 100,
                    parm2: "我是string",
                    parm3: true,
                    parm4: 1.23,
                    parm5: 9.999999
                };
        后台Action方法参数(parm1,parm2...)
        /*传递model(一个对象)：数据对象obj的属性名对应某类对象的属性*/
        前台js:
        var obj = {
                    UserName: '张三',
                    UserPassWord: '不告诉你',
                    UserSex: '男',
                    UserPhone: '138888888888'
                };
        后台Action方法参数(UserInfo ca)
        /*传递多个不同对象*/
        前台js:
        var userinfoObj = {
                    UserName: '张三',
                    UserPassWord: '不告诉你',
                    UserSex: '男',
                    UserPhone: '138888888888'
                };
        var addressObj = {
            Country: '中国',
            Province: '江西',
            City: '南昌',
            Street: '红谷滩新区XX路XX号'
        };
        Ajax函数里data参数：
            data: JSON.stringify({
                userinfo: userinfoObj,
                address: addressObj
            }),
        后台Action方法参数(UserInfo userinfo, Address address)
        /*传递对象集合*/
        var model = [];  
        $.each($("table tr"), function (i, item) {  
            var RTONumber = $(item).find("[name=rtoNumber]").val();  
            var Approver = $(item).find("[name=approver]").val();  
            var Modifier = $(item).find("[name=modifier]").val();  
            var Comment = $(item).find("[name=comment]").val();  
            model.push({ rtoNumber: RTONumber, approver: Approver, modifier: Modifier, comment: Comment});  
        }); 
        Ajax函数里data参数：data: JSON.stringify(model)
    * 3.3 若有返回值，Controller实现
        * return Json(返回的数据);//Json返回值类型System.Web.Mvc.JsonResult:System.Web.Mvc.ActionResult
        
* 4.参考
    * http://www.cnblogs.com/sunxi/p/4484440.html
    * http://www.cnblogs.com/BluceLee/p/3701283.html
    * http://www.cnblogs.com/wubh/p/6253358.html
    * http://blog.csdn.net/ydm19891101/article/details/44336951
    * http://blog.csdn.net/hanxuemin12345/article/details/38872807
    * http://blog.csdn.net/zjx86320/article/details/42555223
```
	
## asp.net-webform

``` html
### ASP.NET常用类
* Page类
    * ASP.NET页面生命周期（所有Web页面都继承自System.Web.UI.Page类）
        * 1）客户机向Web应用程序发送一个页面请求
        * 2）服务器端Web应用程序接收这个请求，先看这个页面是否编译过，若未编译就编译这个Web页面成一个类（该类继承自Page类），然后实例化该类产生一个Page对象
        * 3）Page对象根据客户请求，把信息返回给IIS，然后信息由IIS返回给客户机（执行顺序：Page_Load()方法——（若有事件，执行事件对应的方法）——aspx页面上的服务器代码）
    * 常用属性和方法
        * IsPostBack（是否回传）if(!IsPostBack){}
        * (Request，Response，Server，Session，Application，ViewState)获取当前Page实例
        * MapPath()，ResolveUrl()，DataBind()，Dispose()
* Request类（获取发送请求端的各种信息）
    * System.Web.HttpRequest类（注意区分HttpWebRequest，此类可以模拟客户机发送请求，功能完整强大）
    * 常用属性和方法
        * Browser（浏览器信息）
        * Url
        * Files（客户端上传的文件集合）
        * Cookies（客户端发送的cookie集合）
        * Form（form表单以POST方式提交的数据集合）
        * QueryString（各种以GET方式提交的数据集合，包括以url地址?后的参数值）
        * ServerVariables（各种信息集合）
        * Request["参数名"]上述4种的集合，索引器
            public string this[string key]
            {
                get
                {
                    string str = this.QueryString[key];
                    if( str != null ) {
                        return str;
                    }
                    str = this.Form[key];
                    if( str != null ) {
                        return str;
                    }
                    HttpCookie cookie = this.Cookies[key];
                    if( cookie != null ) {
                        return cookie.Value;
                    }
                    str = this.ServerVariables[key];
                    if( str != null ) {
                        return str;
                    }
                    return null;
                }
            }
* Response类（Web服务器对客户机请求的响应），响应推送的内容在html文档之前
    * System.Web.HttpResponse类（区分HttpWebResponse）
    * 属性方法
        * Cache等等...
        * Redirect() 重定向到新的URL
        * Write() 将信息写入HTTP响应输出流
        * WriteFile() 将文件写入HTTP响应输出流
        * 其他Clear()，Close()，Flush()，End()等等...
* Server类（与服务器相关的信息）
    * System.Web.HttpServerUtility类
    * 属性方法
        * Execute() 执行指定虚拟路径的处理程序
        * HtmlDecode() 对Html文本解码
        * HtmlEncode() 对Html文本编码
        * UrlDecode() 对（Url中的客户机发送的已编码字符串）进行解码
        * UrlEncode() 编码字符串，以便通过Url从服务器通过HTTP可靠的发送到客户机
        * Transfer() 终止当前页的执行，并为当前请求开始执行新页
* Cookie类（Cookies是服务器把少量数据存储到客户机，和从客户机读取数据的一种技术）
    * System.Web.HttpCookie类
    * 属性方法
* Session类（为每个用户的会话存储信息）
* ViewState类

### 基本服务器控件
* Html的文本标签实在客户机浏览器引发和处理的，而服务器控件（有runat="server"属性）则是在客户机引发，服务端处理的
* 文本
    * Label
    * Literral静态文本
    * TextBox
    * HyperLink超链接
* 按钮
    * Button
    * LinkButton超链接按钮
    * ImageButton
* 图像
    * Image
    * ImageMap
* 选择
    * CheckBox复选
    * CheckBoxList复选列表
    * RadioButton单选
    * RadioButtonList单选列表
* 列表
    * ListBox列表
    * DropDownList下拉框列表
    * BulletedList项目列表
* 容器控件
    * Panel
    * MultiView
    * PlaceHolder
* 其他
    * Calendar日历
    * AdRotator动态广告
### 服务器验证控件
* RequiredFieldValidator必需项
* CompareValidator与某值比较
* RangeValidator范围检查
* RegularExpressionValidator模式匹配
* CustomValidator自定义方法验证

### 自定义服务器控件（.ascx）

### 数据绑定
* DataBind()方法：Page对象和所有Web控件成员的方法，DataBind()方法将所有子控件绑定到DataSource属性指定的数据源，父控件调用DataBind()方法时，所有子控件都会调用此方法，通常在Page_Load()方法里面调用Page对象的DataBind()方法，因为Page对象是页面上所有控件的父控件
* 简单绑定（可以在aspx页面上直接使用：此页面的类的字段，属性，方法，包含上述的表达式等）
    * <%#字段名%> <%#属性名%> <%#方法名(参数)%>
* 在后台代码中给容纳数据的控件（DropDownList,DataGrid等）的DataSource属性绑定对应的数

### 数据表格服务器控件
* GridView
* DetailView 结合GridView显示表格单行的详细信息
* Repeater 模板分块化的表格控件，自由度高
    /*<asp:Repeater>该控件的开始，所有项必须包含在内*/
    /*<ItemTemplate>此项必须要有，用来包含表体绑定的数据<tr><td>内容</td></tr>*/
    /*<HeaderTemplate>和<FooterTemplate>可以有，必须同时出现，<HeaderTemplate>用来放开头<table>和表头行<tr>,<FooterTemplate>用来放</table>*/
    /*后台<asp:Repeater>的id.DataSource=取到的数据集*/
    /*<%# Eval("字段名")%> 绑定列数据*/
    <asp:Repeater ID="Repeater1" runat="server">
        <HeaderTemplate>
            <table>
                <thead>
                    <tr>
                        <td>姓名</td>
                    </tr>
                </thead>
                <tbody>
        </HeaderTemplate>
        <ItemTemplate>
            <tr>
                <td><%# Eval("字段名") %></td>
            </tr>
        </ItemTemplate>
        <FooterTemplate>
            </tbody></table>
        </FooterTemplate>
    </asp:Repeater>
* DataList 类似Repeater，增加了选定项，编辑项，交替项等
* ListView 类似GridView，但可以使用类似Repeater的模板，高度自由化
* Chart（图表控件）新增

### 网站设计
* 母版页
* 网站导航
* 导航控件
* 主题
* 样式

```
