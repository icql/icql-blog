---
title: java_se_bio
date: 2018-11-05 00:00:00
---
## BIO类概述
* 输入输出：站在内存的角度来讲的，input输入代表输入到内存中，output输出代表从内存中输出
* 流：流从概念上来说是一个连续的数据流。你既可以从流中读取数据，也可以往流中写数据。流与数据源或者数据流向的媒介相关联。在Java IO中流既可以是字节流(以字节为单位进行读写)，也可以是字符流(以字符为单位进行读写)
* 可以根据处理的数据是二进制数据还是字符数据来选择使用 字节流bytes 或者 字符流char

## 字节流：输入流（包含继承关系）
* InputStream（抽象类，所有的输入字节流的父类）
	* FileInputStream（基本介质流，从 本地文件 读取数据）
	* ByteArrayInputStream（基本介质流，从 内存中Byte数组 读取数据）
	* StringBufferInputStream（基本介质流，Deprecated，从 内存中字符串 读取数据）
	* SequenceInputStream（合并流）
	* PipedInputStream（管道流，从与其它线程共用的管道中 读取数据）
	* ObjectInputStream（序列化流）
	* FilterInputStream（装饰流）
		* BufferedInputStream（装饰流）
		* DataInputStream（装饰流）
		* LineNumberInputStream（装饰流）
		* PushbackInputStream（装饰流）

* 基本输入字节流
	* FileInputStream（基本介质流，从 本地文件 读取数据）
		* 功能：最基本的文件输入流。主要用于从文件中读取信息
		* 构造：通过一个代表文件路径的 String、File对象或者 FileDescriptor对象创建
		* 使用：一般作为数据源，同样会使用其它装饰器提供额外的功能
	* ByteArrayInputStream（基本介质流，从 内存中Byte数组 读取数据）
		* 功能：将内存中的Byte数组适配为一个InputStream
		* 构造：从内存中的Byte数组创建该对象（2种方法）
		* 使用：一般作为数据源，会使用其它装饰流提供额外的功能，一般都建议加个缓冲功能
	* StringBufferInputStream（基本介质流，从 内存中字符串 读取数据）
		* 功能：将内存中的字符串适配为一个InputStream
		* 构造：从一个String对象创建该对象，底层的实现使用StringBuffer，Deprecated，主要原因是StringBuffer不应该属于字节流，所以推荐使用StringReader
	* SequenceInputStream（合并流）
		* 功能：将2个或者多个InputStream 对象转变为一个InputStream
		* 构造：使用两个InputStream 或者内部对象为InputStream 的Enumeration对象创建该对象
		* 使用：一般作为数据源，同样会使用其它装饰器提供额外的功能

* 管道输入字节流

* 序列化输入字节流 http://ifeve.com/java-io-s-objectinputstream-objectoutputstream/ 
	* transient 关键字 修饰不想被序列化的字段

* 装饰输入字节流
	* FilterInputStream（装饰流，给其它被装饰对象提供额外功能的抽象类）
		* BufferedInputStream（装饰流）
			* 功能：使用该对象阻止每次读取一个字节都会频繁操作IO。将字节读取到一个缓冲区，从缓冲区读取，缓冲区是其内部维护一个字节数组，默认大小是 byte[8*1024] 8kb大小，而从底层流中读取数据的操作还是调用InputStream的方法完成
			* 构造：利用一个InputStream、或者带上一个自定义的缓存区的大小构造
			* 使用：使用InputStream的方法读取，只是背后多一个缓存的功能。设计模式中透明装饰器的应用
		* DataInputStream（装饰流）
			* 功能：一般和DataOutputStream配对使用,完成基本数据类型的读写
			* 构造：利用一个InputStream构造
			* 使用：提供了大量的读取 基本数据类型 的读取方法
		* LineNumberInputStream（装饰流）
			* 功能：跟踪输入流中的行号。可以调用getLineNumber( ) 和 setLineNumber(int)方法得到和设置行号
			* 构造：利用一个InputStream构造
			* 使用：紧紧增加一个行号，可以像使用其它InputStream一样使用
		* PushbackInputStream（装饰流）
			* 功能：可以在读取最后一个byte 后将其放回到缓存中
			* 构造：利用一个InputStream构造
			* 使用：一般仅仅会在设计 compiler 的 scanner 时会用到这个类，一般用不到

## 字节流：输出流（包含继承关系）
* OutputStream（抽象类，所有的输出字节流的父类）
	* FileOutputStream（基本介质流，向 本地文件 写入数据）
	* ByteArrayOutputStream（基本介质流，向 内存中Byte数组 写入数据）
	* PipedOutputStream（管道流，向 与其它线程共用的管道中 写入数据）
	* ObjectOutputStream（序列化流）
	* FilterOutputStream（装饰流）
		* BufferedOutputStream（装饰流）
		* DataOutputStream（装饰流）
		* PrintStream（装饰流）

* 基本输出字节流
	* FileOutputStream（基本介质流，向 本地文件 写入数据）
		* 功能：将信息写入文件中
		* 构造：使用代表文件路径的String、File对象或者 FileDescriptor 对象创建。还可以加一个代表写入的方式是否为append的标记
		* 使用：一般将其和FilterOutputStream套接得到额外的功能
	* ByteArrayOutputStream（基本介质流，向 内存中Byte数组 写入数据）
		* 功能：在内存中创建一个缓冲区buffer。所有写入此流中的数据都被放入到此buffer中
		* 构造：无参或者使用一个可选的初始化buffer的大小的参数构造
		* 使用：一般将其和 FilterOutputStream 套接得到额外的功能。建议首先和 BufferedOutputStream 套接实现缓冲功能。通过 toByteArray 方法可以得到流中的数据。（不透明装饰器的用法）

* 管道输出字节流

* 序列化输出字节流 http://ifeve.com/java-io-s-objectinputstream-objectoutputstream/

* 装饰输出字节流
	* FilterOutputStream（装饰流，给其它被装饰对象提供额外功能的抽象类）
		* BufferedOutputStream（装饰流）
			* 功能：使用它可以避免频繁地向IO写入数据，数据一般都写入一个缓存区，在调用flush方法后会清空缓存、一次完成数据的写入
			* 构造：从一个 OutputStream 或者和一个代表缓存区大小的可选参数构造
			* 使用：提供和其它OutputStream一致的接口，只是内部提供一个缓存的功能
		* DataOutputStream（装饰流）
			* 功能：通常和DataInputStream配合使用，使用它可以写入基本数据类型
			* 构造：使用OutputStream构造
			* 使用：包含大量的写入基本数据类型的方法
		* PrintStream（装饰流）
			* 功能：产生具有格式的输出信息
			* 构造：使用OutputStream和一个可选的表示缓存是否在每次换行时是否flush的标记构造。还提供很多和文件相关的构造方法
			* 使用：一般是一个终极（“final”）的包装器，很多时候我们都使用它

## 字节流：输入与输出的对应
* 字节流的输入和输出大部分一一对应
* 不太对称的几个类
	* LineNumberInputStream：主要完成从流中读取数据时，会得到相应的行号，至于什么时候分行、在哪里分行是由该类主动确定的，并不是在原始中有这样一个行号。在输出部分没有对应的部分，我们完全可以自己建立一个LineNumberOutputStream，在最初写入时会有一个基准的行号，以后每次遇到换行时会在下一行添加一个行号，看起来也是可以的，此类作用不大
	* PushbackInputStream：主要功能是查看最后一个字节，不满意就放入缓冲区。主要用在编译器的语法、词法分析部分。输出部分的BufferedOutputStream几乎实现相近的功能
	* StringBufferInputStream：已经被Deprecated，本身就不应该出现在InputStream部分，主要因为String应该属于字符流的范围。已经被废弃了，当然输出部分也没有必要需要它了，还允许它存在只是为了保持版本的向下兼容而已
	* SequenceInputStream：可以认为是一个工具类，将两个或者多个输入流当成一个输入流依次读取
	* PrintStream：也可以认为是一个辅助工具。主要可以向其他输出流，或者FileInputStream写入数据，本身内部实现还是带缓冲的。本质上是对其它流的综合运用的一个工具而已。System.out和System.err就是PrintStream的实例
* ObjectInputStream/ObjectOutputStream和DataInputStream/DataOutputStream主要是要求写对象/数据和读对象/数据的次序要保持一致，否则轻则不能得到正确的数据，重则抛出异常(一般会如此)
* PipedInputStream/PipedOutputStream在创建时一般就一起创建，调用它们的读写方法时会检查对方是否存在，或者关闭！道理极其简单――对方都不在了，怎么交互

## 字符流：输入流（包含继承关系）
* Reader（抽象类，所有的输入字符流的父类）
	* InputStreamReader（是一个连接字节流和字符流的桥梁，它将字节流转变为字符流），实例化Reader的子类时一般要用它
		* FileReader（文件流）
	* CharArrayReader（基本介质流，从 内存中Char数组 读取数据）
	* StringReader（基本介质流，从 内存中字符串 读取数据）
	* PipedReader（管道流）
	* BufferedReader（装饰流，经常用）
	* FilterReader（装饰流）
		* PushbackReader

## 字符流：输出流（包含继承关系）
* Writer（抽象类，所有的输入字符流的父类）
	* OutputStreamWriter（是一个连接字节流和字符流的桥梁，它将字节流转变为字符流），实例化Writer的子类时一般要用它
		* FileWriter（文件流）
	* CharArrayWriter（基本介质流，从 内存中Char数组 读取数据）
	* StringWriter（基本介质流，从 内存中字符串 读取数据）
	* PipedWriterr（管道流）
	* BufferedWriter（装饰流）
	* PrintWriter（装饰流，经常用）
	* FilterWriter（装饰流）