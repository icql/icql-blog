---
title: html_css_js归档
date: 2019-03-15 00:00:00
---


## html

``` html
1.响应式布局
	RWD 指的是响应式 Web 设计（Responsive Web Design）
	RWD 能够以可变尺寸传递网页
	RWD 对于平板和移动设备是必需的

2.<head></head>头部元素:
	<title>标题
	<style>样式

	<meta>元数据() 
	为搜索引擎定义关键词:
	<meta name="keywords" content="HTML, CSS, XML, XHTML, JavaScript">
	为网页定义描述内容:
	<meta name="description" content="Free Web tutorials on HTML and CSS">
	定义网页作者:
	<meta name="author" content="Hege Refsnes">
	每30秒中刷新当前页面:
	<meta http-equiv="refresh" content="30">

	<link>定义了文档与外部资源之间的关系，常用于css文件和文档关系<link rel="stylesheet" href="">
	<script>, script脚本语言
	<noscript>,
	<base> 基本的链接地址/链接目标，该标签作为HTML文档中所有的链接标签的默认链接,用于简写body内的相对地址；
3.文本格式化（Formatting）
	<b>粗体文本</b>
	<strong>
	<code>计算机代码</code>
	<em>强调文本</em>
	<i>斜体文本</i>
	<kbd>键盘输入</kbd> 
	<pre>预格式化文本</pre>
	<small>更小的文本</small>
	<strong>重要的文本</strong>
	 
	<abbr> （缩写）
	<address> （联系信息）
	<bdo> （文字方向）
	<blockquote> （从另一个源引用的部分）
	<cite> （工作的名称）
	<del> （删除的文本）
	<ins> （插入的文本）
	<sub> （下标文本）
	<sup> （上标文本）

4.<img>  src alt 尽量都有

5.<table> 属性border=""边框 <tr>行,<td>单元格数据,<th>表头

6.<ol> <ul> <li>,自定义列表:<dl>,<dd>

7.块级元素:通常以新行开始,<h1><p><ul><table><ol>
  内联元素:通常不会以新行开始,<img><a><b><td>
  <div>属块级元素,会折行,HTML元素的容器
  <span>属内联元素,不会折行,文本的容器

8.HTML布局:尽量使用div和css进行布局

9.表单:<form>,<input>,type有文本域text,密码字段password,单选按钮radio,复选框checkbox,提交按钮submit
  
  HTML5新标签:
	<form>		定义供用户输入的表单

	<fieldset>	定义了一组相关的表单元素，并使用外框包含起来
	<legend>	定义了 <fieldset> 元素的标题
	
	<label>		定义了 <input> 元素的标签，一般为输入标题
	<input>		定义输入域

	<select>	定义了下拉选项列表
	<optgroup>	定义选项组
	<option>	定义下拉列表中的选项

	<button>	定义一个点击按钮
	<textarea>	定义文本域 (一个多行的输入控件)

	<datalist>New	指定一个预先定义的输入控件选项列表
	<keygen>New	定义了表单的密钥对生成器字段
	<output>New	定义一个计算结果

10.框架:使用框架，可以在同一个浏览器窗口中显示不止一个页面
	<iframe src="url" style="width: ;height= ;">基本的
	可选属性:frameborder="number",边框

11.颜色:颜色名/十六进制#(三位或六位)

12.url统一资源定位器(Uniform Resource Locators)
	域名:baidu.com
	互联网协议IP地址:192.123.12

	scheme://host.domain:port/path/filename
	说明:
		scheme - 定义因特网服务的类型。最常见的类型是 http
		host - 定义域主机（http 的默认主机是 www）
		domain - 定义因特网域名，比如 runoob.com
		:port - 定义主机上的端口号（http 的默认端口号是 80）
		path - 定义服务器上的路径（如果省略，则文档必须位于网站的根目录中）。
		filename - 定义文档/资源的名称

	以下是一些URL scheme：
	http	超文本传输协议		以 http:// 开头的普通网页。不加密。
	https	安全超文本传输协议	安全网页，加密所有信息交换。
	ftp	文件传输协议		用于将文件下载或上传至网站。
	file	 			您计算机上的文件。
```

## css

``` html
1.html5
	2014年,万维网联盟W3C和WHATWG共同制定

	<!DOCTYPE html> 声明HTML5文档
2.html5兼容:
	header, section, footer, aside, nav, main, article, figure.(块级)

	旧浏览器正确显示新元素:利用CSS的属性display:设置为block块级元素.

	自定义新元素(在css里自定义)

	解决ie兼容html5:使用shiv
	国外:
	<!--[if lt IE 9]>
		<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	国内:
	<!--[if lt IE 9]>
		<script src="http://cdn.static.runoob.com/libs/html5shiv/3.7/html5shiv.min.js"></script>
	<![endif]-->

	放入head中.


新元素

3.<canvas> 标签定义图形(画布)，比如图表和其他图像。属性  width,height
	<canvas>输出图形容器,然后用JavaScript 的绘图 API进行绘图.

	使用顺序(script中):
		var c=document.getElementByID("");找到画布
		var ctx=c.getContext("2d");创建可用于在画布上绘图的方法和属性的对象
		...操作画图
	
4.<svg> 基于XML
	SVG 指可伸缩矢量图形 (Scalable Vector Graphics)
	SVG 用于定义用于网络的基于矢量的图形
	SVG 使用 XML 格式定义图形
	SVG 图像在放大或改变尺寸的情况下其图形质量不会有损失
	SVG 是万维网联盟的标准

	SVG优势:
		与其他图像格式相比（比如 JPEG 和 GIF），使用 SVG 的优势在于：
		SVG 图像可通过文本编辑器来创建和修改
		SVG 图像可被搜索、索引、脚本化或压缩
		SVG 是可伸缩的
		SVG 图像可在任何的分辨率下被高质量地打印
		SVG 可在图像质量不下降的情况下被放大
	
	Canvas 与 SVG 的比较:
		1)SVG 是一种使用 XML 描述 2D 图形的语言。Canvas 通过 JavaScript 来绘制 2D 图形。
		2)SVG 基于 XML,SVG DOM 中的每个元素都是可用的,支持事件处理器.canvas不支持事件处理器
		3)SVG 不依赖分辨率.Canvas依赖分辨率

5.<math> 数学标记语言,基于XML
6.拖放
7.地理定位
8.视频音频
	<video> src,width,height,type="video/mp4" ,controls="controls"(播放控件),也可以利用javascript中的DOM进行播放控制
	<audio>

9.输入类型(新):<input type="color" name="">
	color 颜色拾取器
	date 日期
	datetime UTC时间
	datetime-local 日期和时间(无时区)
	email 邮件
	month 月份
	number 数值(有诸多属性进行限制,例如mix,max)
	range  滑动条数值域,min,max
	search 搜索
	tel 电话
	time 时间(无时区)
	url url地址
	week 年周

10.表单:
	新类型<datalist><keygen><output>
	新属性  <form> / <input>  autocomplete 属性(第一次填写,刷新后第二次就会自动补全)
		<input> multiple 属性,多选
		<input> step 属性(number)
11.语义元素(新)
	<header>
	<nav>导航
	<section>
	<article>
	<aside>侧边栏
	<figcaption>标签定义 <figure> 元素的标题
	<figure>标签规定独立的流内容（图像、图表、照片、代码等等）
	<footer>

12.Web 存储(JSON)
13.Web SQL
14.应用程序缓存
15.Web Workers
16.SSE
17.WebSocket


margin(外边距):
margin:20px auto;  和  margin:auto 20px; 区别


每种浏览器为body设置了默认样式:内外边距

块元素填满整个窗口:body本身有外边距


块级元素特点：
        1、每个块级元素都从新的一行开始，并且其后的元素也另起一行。（真霸道，一个块级元素独占一行）

        2、元素的高度、宽度、行高以及顶和底边距都可设置。

        3、元素宽度在不设置的情况下，是它本身父容器的100%（和父元素的宽度一致），除非设定一个宽度。
内联块级元素:<img><input>  inline-block          
        1、和其他元素都在一行上；
        2、元素的高度、宽度、行高以及顶和底边距都可设置。

内联元素特点：
        1、和其他元素都在一行上；

        2、元素的高度、宽度及顶部和底部边距不可设置；

        3、元素的宽度就是它包含的文字或图片的宽度，不可改变。

内联元素的样式只要设置了
                1. position : absolute 
                2. float : left 或 float:right 
                就会将其改变为内联块级元素.
样式缩写:1.margin/padding
        2.颜色值缩写
        2.字体缩写:font:12px/1.5em "宋体",sans-serif;
                1、使用这一简写方式你至少要指定 font-size 和 font-family 属性，其他的属性(如 font-weight、font-style、font-variant、line-height)如未指定将自动使用默认值。
                2、在缩写时 font-size 与 line-height 中间要加入“/”斜扛。
            
            
 尺寸:px像素
    em:相对尺寸:1em,父元素大小;  1.5em,父元素大小的1.5倍 
    
    
水平居中设置:
    1.行内元素:例如文本和图片,通过设置其  父元素 text-align:center 来实现的(文本本身就是元素,无需标签)
    2.块级元素:
        1):定宽块状元素:块状元素的宽度 width 是固定值,利用 左右margin值为0  来设置
        2):不定宽块状元素:块状元素的宽度 width 不是固定值,有 3 种方法:
            (1)加入table标签:利用 table 的长度自适应性(不定义长度也不会默认是其父元素或body的长度,而是根据其内文本的长度决定的),在 该元素上 嵌套一个  <table> ,设置<table>的样式为 margin:0 auto;(看成定宽的块级元素)
            (2)改变元素的 display 类型为行内元素,display:inline,设置 text-align:center
            (3)通过给父元素设置 float，然后给父元素设置 position:relative 和 left:50%，子元素设置 position:relative 和 left: -50% 来实现水平居中。

垂直居中设置:
    1.父元素高度确定的单行文本:设置父元素的 height 和 line-height 高度一致来实现的。(height: 该元素的高度，line-height: 顾名思义，行高（行间距），指在文本中，行与行之间的 基线间的距离 )。
    
    2.父元素高度确定的多行文本、图片等:
    (1)方法一：使用插入 table (包括tbody、tr、td)标签，同时设置table vertical-align：middle。

css布局模型:
        结构样式行为  分离

        1.流动模型(flow)标准文档流
            默认布局:从上到下,从左到右
                    块级元素自上而下按顺序垂直分布,默认宽度100%
                    内联元素都会在所处的包含元素内从左到右水平分布显示
                    
        2.浮动模型(float)
            要浮动的元素都要设置float,两元素的水平间隔,取决于第一个元素的外边距大小
    设置了float属性的块级元素会对 紧随其后的元素(仅仅一个)   产生影响,也有可能对其父元素产生影响
    
    清除浮动:1)对受到影响的元素设置 clear:both  或者  clear:left , right(主要用于清除后面的元素)
            2)对受到影响的元素设置 width:100% 和 overflow:hidden //溢出盒子时执行的操作
            (两种皆可)
    
        3.层模型(layer)
            累死ps中的图层
            
            绝对定位:position:absolute  相对于其最接近的一个  具有定位属性()  的父包含块,进行绝对定位,没有则默认为以根目录为基准,完全飘逸出文档流.
            
            相对定位:position:relative  相对定位完成的过程是首先按static(float)方式生成一个元素(并且元素像层一样浮动了起来)，然后    相对于   以前的位置移动;  偏移前的位置保留不动(后面的元素仍然按照之前的位置进行定位)。
            
            固定定位:position:fixed     表示固定定位，与absolute定位类型类似，但它的相对移动的坐标是视图（屏幕内的网页窗口）本身。
            
            
            参照父元素进行定位:利用 absolute和relative;父元素必须设置position:absolute;定位元素就可以利用 position:absolute进行定位.
           
```

## javascript

``` javascript
1.语句
	分号;
2.变量
	局部,全局
	变量必须以字母开头
	变量也能以 $ 和 _ 符号开头（不过我们不推荐这么做）
	不写var的做法，不利于表达意图，而且容易不知不觉地创建全局变量，所以建议总是使用var命令声明变量
	变量提升
		js中，变量可以在使用后声明，也就是变量可以先使用再声明(var)
			*只有声明的变量会提升(var)
3.标识符
	变量名,函数名,id名
	第一个字符，英文字母和其他语言的字母 $ _
	第二个字符及后面的字符，除了以上,还可以用数字0-9
	对大小写敏感
	首单词首字母小写,后面的单词首字母大写
4.注释
	//单行
	/* */多行
	<!--  -->兼容html注释
5.区块
	使用大括号，将多个相关的语句组合在一起，称为“区块”（block）
	非函数,流程控制语句区块,变量作用域与区块外属于同一个作用域
	单独{语句},实际实用意义不大
6.运算符
	加法运算符+
		*两个运算子只要有一个运算子是字符串，则两个运算子都转为字符串，执行字符串连接运算,x+""  转换为字符串
		两个运算子都转为数值，执行加法运算
	算术运算符
		加法运算符（Addition）：x + y
		减法运算符（Subtraction）： x - y
		乘法运算符（Multiplication）： x * y
		除法运算符（Division）：x / y
		余数运算符（Remainder）：x % y	
			运算结果的正负号由第一个运算子的正负号决定。-1 % 2 // -1	1 % -2 // 1
		自增运算符（Increment）：++x 或者 x++
			y=5;x=y++;
			得到:x=5,y=6
			y=5;x=++y;
			得到:x=6,y=6
		自减运算符（Decrement）：--x 或者 x--
		数值运算符（Convert to number）： +x
		负数值运算符（Negate）：-x
	赋值运算符
		x += y // 等同于 x = x + y
		x -= y // 等同于 x = x - y
		x *= y // 等同于 x = x * y
		x /= y // 等同于 x = x / y
		x %= y // 等同于 x = x % y
		x >>= y // 等同于 x = x >> y
		x <<= y // 等同于 x = x << y
		x >>>= y // 等同于 x = x >>> y
		x &= y // 等同于 x = x & y
		x |= y // 等同于 x = x | y
		x ^= y // 等同于 x = x ^ y
	比较运算符
		== 相等
		=== 严格相等
		!= 不相等
		!== 严格不相等
		< 小于
		<= 小于或等于
		> 大于
		>= 大于或等于
	布尔运算符
		取反运算符：!	返回布尔值true或false
		且运算符：&&
			如果第一个运算子的布尔值为true，则返回第二个运算子的值（注意是值，不是布尔值）；如果第一个运算子的布尔值为false，则直接返回第一个运算子的值，且不再对第二个运算子求值。
		或运算符：||
			如果第一个运算子的布尔值为true，则返回第一个运算子的值，且不再对第二个运算子求值；如果第一个运算子的布尔值为false，则返回第二个运算子的值。
		三元运算符：()?():()
			三元条件运算符用问号（?）和冒号（:），分隔三个表达式。如果第一个表达式的布尔值为true，则返回第二个表达式的值，否则返回第三个表达式的值。
	位运算符(只对整数有效，遇到小数时，会将小数部分舍去，只保留整数部分)
		或运算（or）：符号为|，表示若两个二进制位都为0，则结果为0，否则为1
		与运算（and）：符号为&，表示若两个二进制位都为1，则结果为1，否则为0
		否运算（not）：符号为~，表示对一个二进制位取反。
		异或运算（xor）：符号为^，表示若两个二进制位不相同，则结果为1，否则为0
		左移运算（left shift）：符号为<<，将一个数的二进制值向左移动指定的位数，尾部补0，即乘以2的指定次方
		右移运算（right shift）：符号为>>，将一个数的二进制值向右移动指定的位数，头部补0，即除以2的指定次方
		带符号位的右移运算（zero filled right shift）：符号为>>>，正数和右移运算一样,负数将符号位改为0
	其他运算符
		void运算符
			javascript:void(代码),void运算符的作用是执行一个表达式，然后不返回任何值，或者说返回undefined
		逗号运算符
			逗号运算符用于对两个表达式求值，并返回后一个表达式的值	'a', 'b' // "b"
7.流程控制语句
	(1)
	if ()
	{}
	else
	{}
	(2)
	switch()
	{
	case value1:
		执行语句;
		break;
	case value2:
		执行语句;
		break;
	default:
		默认执行语句;
	} 
	(3) for - 循环代码块一定的次数
		for/in - 循环遍历对象的属性
		while - 当指定的条件为 true 时循环指定的代码块
		入口条件循环

		do/while - 同样当指定的条件为 true 时循环指定的代码块,至少循环一次,是退出条件循环
		for(;;){}
		for(x in 对象){var demo=demo + 对象[x]; }
	(4)break 语句可用于跳出循环。
		continue 语句跳出循环后(跳出本次迭代)，会继续循环之后的代码

```

``` javaScript
1.数据类型
	在 JavaScript 中有 5 种不同的数据类型：
		string字符串
		number数值	包括NaN       
		boolean布尔
		object对象
		function函数

	3 种对象类型：
		Object普通对象
		Date时间对象
		Array数组对象

	2 个不包含任何值的数据类型：
		null是一个只有一个值的特殊类型。表示一个空对象引用
		undefined 是一个没有设置值的变量

	typeof  查看 JavaScript 变量的数据类型
	constructor 属性返回所有 JavaScript 变量的构造函数;
2.数值
	JavaScript内部，所有数字都是以64位浮点数形式储存，即使整数也是如此。所以，1与1.0是相同的，是同一个数。
	浮点型数据使用,所有的编程语言，包括JavaScript，对浮点型数据的精确度都很难确定
	在JavaScript语言的底层，根本没有整数，所有数字都是小数（64位浮点数）
		第1位：符号位，0表示正数，1表示负数
		第2位到第12位：储存指数部分
		第13位到第64位：储存小数部分（即有效数字）
	JavaScript提供的有效数字最长为51个二进制位
	进制
		十进制：没有前导0的数值。
		八进制：有前缀0o或0O的数值，或者有前导0、且只用到0-7的七个阿拉伯数字的数值。
		十六进制：有前缀0x或0X的数值。
		二进制：有前缀0b或0B的数值
	0除以0也会得到NaN
	+0和-0基本一样,但做分母时,非0除以0,除以正零得到+Infinity，除以负零得到-Infinity
	NaN是JavaScript的特殊值，表示“非数字”（Not a Number），主要出现在将字符串解析成数字出错的场合。NaN不等于任何值，包括它本身。NaN === NaN // false
	数值相关全局方法:
		parseInt()方法
			用于将字符串转为整数
			parseInt方法还可以接受第二个参数（2到36之间），表示被解析的值的进制，返回该值对应的十进制数。默认情况下，parseInt的第二个参数为10，即默认是十进制转十进制
		parseFloat()方法用于将一个字符串转为浮点数
3.字符串
	尽量使用单引号''
	分行用 \
	转义
		\0 null（\u0000）
		\b 后退键（\u0008）
		\f 换页符（\u000C）
		\n 换行符（\u000A）
		\r 回车键（\u000D）
		\t 制表符（\u0009）
		\v 垂直制表符（\u000B）
		\' 单引号（\u0027）
		\" 双引号（\u0022）
		\\ 反斜杠（\u005C）
	字符串可以被视为字符数组，因此可以使用数组的方括号运算符，用来返回某个位置的字符（位置编号从0开始）。
	但是，字符串与数组的相似性仅此而已。实际上，无法改变字符串之中的单个字符。
	length属性
4.对象
	对象:就是一种无序的数据集合，由若干个“键值对”（key-value）构成
		是属性和方法的容器
		var o1 = {};
		var o2 = new Object();
		var o3 = Object.create(Object.prototype);
	键名:
		对象的所有键名都是字符串，所以加不加引号都可以
		但是，如果键名不符合标识名的条件（比如第一个字符为数字，或者含有空格或运算符），也不是数字，则必须加上引号，否则会报错。
		注意，JavaScript的保留字可以不加引号当作键名,不建议这么做
	属性:
		对象的每一个“键名”又称为“属性”（property），它的“键值”可以是任何数据类型。
		如果一个属性的值为函数，通常把这个属性称为“方法”，它可以像函数那样调用
		读取属性:
			使用点运算符,数值键名不能使用点运算符（因为会被当成小数点），只能使用方括号运算符
			使用方括号运算符(如果使用方括号运算符，键名必须放在引号里面，否则会被当作变量处理。但是，数字键可以不加引号，因为会被当作字符串处理)方括号运算符内部可以使用表达式
		属性的赋值:
			属性可以动态创建，不必在对象声明时就指定
			利用点运算符和方括号运算符进行赋值

	对象的引用:
		如果不同的变量名指向同一个对象，那么它们都是这个对象的引用，也就是说指向同一个内存地址。修改其中一个变量，会影响到其他所有变量。
			var o1 = {};
			var o2 = o1;
			o1.a = 1;
			o2.a // 1
			o2.b = 2;
			o1.b // 2
	单独的对象{}:
		为避免和代码块产生歧义,对象用({})
	Object.keys(对象名)方法:查看一个对象本身的所有属性
	delete命令:用于删除对象的属性，删除成功后返回true,delete o.p 
		delete命令只能删除对象本身的属性，无法删除继承的属性
	in运算符:用于检查对象是否包含某个属性（注意，检查的是键名，不是键值），如果包含就返回true，否则返回false,不能识别对象继承的属性
		var o = { p: 1 };
		'p' in o // true
	for...in循环
	with语句

5.函数
	声明:
		function 函数名(){}
		var 变量名=function(){}
		var 变量名= new Function(){}
	变量声明时如果不使用var关键字，那么它就是一个全局变量，即便它在函数内定义
	return
		遇到return语句，就直接返回return后面的那个表达式的值，后面即使还有语句，也不会得到执行
	函数提升:类似变量提升
	不能在条件语句中声明函数
	函数的属性和方法:
		name属性:返回紧跟在function关键字之后的那个函数名
		length属性:返回函数预期传入的参数个数，即函数定义之中的参数个数
		toString方法:返回函数的源码
	作用域
	参数
		js允许传递不完整参数或不传参数
		可设置默认值,利用()?():() 利用undefineed
		arguments对象:只能在函数内部使用,arguments[0]代表第一个参数,以此类推
	闭包:在函数外部读取函数内部声明的变量
		定义函数f1,在函数内部定义函数f2,return返回函数f2,闭包就是函数f2
		不能滥用闭包，否则会造成网页的性能问题
		用处:
			读取函数内部的变量
			使得内部变量记住上一次调用时的运算结果
			封装对象的私有属性和私有方法
	自执行函数
		通常情况下，只对匿名函数使用这种“立即执行的函数表达式”。它的目的有两个：一是不必为函数命名，避免了污染全局变量；二是IIFE内部形成了一个单独的作用域，可以封装一些外部无法读取的私有变量
		变为表达式就可以使用:
			(function(){}());
			var cc=function(){}();
			new function(){ /* code */ }();
	eval命令
		eval命令的作用是，将字符串当作语句执行
6.类型转换
	强制转换:Number、String和Boolean三个构造函数
	自动转换
		遇到预期为布尔值的地方
		遇到预期为字符串的地方
		遇到预期为数值的地方
7.错误处理机制
	throw,try,catch
8.编程习惯
	避免使用全局变量
	少用new关键字创建对象,必须手动释放内存,不会自动回收

```

``` javaScript
1.Object
	JavaScript原生提供一个Object对象（注意起首的O是大写），所有其他对象都继承自这个对象
	构造函数,用来生成新的对象	var haHa= new Object();
	静态方法:部署在Object对象(原生Object)本身
		Object.keys(对象名)	遍历对象的属性,只返回可枚举的属性
		Object.getOwnPropertyNames(对象名)	遍历对象的属性,还返回不可枚举的属性名
		JavaScript没有提供计算对象属性个数的方法，所以可以用这两个方法代替
		Object.keys(对象名).length
		Object.getOwnPropertyNames(对象名).length
		（1）对象属性模型的相关方法
			Object.getOwnPropertyDescriptor()：获取某个属性的attributes对象。
			Object.defineProperty()：通过attributes对象，定义某个属性。
			Object.defineProperties()：通过attributes对象，定义多个属性。
			Object.getOwnPropertyNames()：返回直接定义在某个对象上面的全部属性的名称。
		（2）控制对象状态的方法
			Object.preventExtensions()：防止对象扩展。
			Object.isExtensible()：判断对象是否可扩展。
			Object.seal()：禁止对象配置。
			Object.isSealed()：判断一个对象是否可配置。
			Object.freeze()：冻结一个对象。
			Object.isFrozen()：判断一个对象是否被冻结。
		（3）原型链相关方法
			Object.create()：生成一个新对象，并该对象的原型。
			Object.getPrototypeOf()：获取对象的Prototype对象

	实例方法:部署在Object.prototype对象,指向一个原型对象,凡是定义在Object.prototype对象上面的属性和方法，将被所有实例对象共享
		对象名.实例方法()
		主要有以下6个:
			valueOf()：返回当前对象对应的值。默认情况下返回对象本身
			toString()：返回当前对象对应的字符串形式。默认情况下返回类型字符串
			toLocaleString()：返回当前对象对应的本地字符串形式。
			hasOwnProperty()：判断某个属性是否为当前对象自身的属性，还是继承自原型对象的属性。
			isPrototypeOf()：判断当前对象是否为另一个对象的原型。
			propertyIsEnumerable()：判断某个属性是否可枚举。
		
2.包装对象
	3种原始类型的值——数值、字符串、布尔值
	3个原生对象Number、String、Boolean,原生对象可以把原始类型的值变成（包装成）对象,使得原始类型的值可以方便地调用特定方法
	Number、String和Boolean这三个对象作为构造函数使用（带有new）时，可以将原始类型的值转为对象；作为普通函数使用时（不带有new），可以将任意类型的值，转为原始类型的值

	原始类型的自动转换
		原始类型的值，可以自动当作对象调用，即调用各种对象的方法和参数。这时，JavaScript引擎会自动将原始类型的值转为包装对象，在使用后立刻销毁。

3.Boolean对象
4.Number对象
	属性:
	实例方法
		通用对象的6种实例方法
		toFixed(指定位数参数)方法用于将一个数转为指定位数的小数，返回这个小数对应的字符串
		toExponential()方法用于将一个数转为科学计数法形式
		toPrecision()方法用于将一个数转为指定位数的有效数字
	注意: (100).方法名 直接使用时在数字外加括号,不然.会当作小数点

5.String对象
	属性:length
	静态方法:String.fromCharCode()参数是一系列Unicode码点，返回对应的字符串
		String.fromCharCode(104, 101, 108, 108, 111)
		// "hello"
	实例方法:
		charAt()方法返回指定位置的字符，参数是从0开始编号的位置,完全可以用数组下标替代'abc'[0]
		concat方法用于连接两个字符串，返回一个新字符串，不改变原字符串
		slice方法用于从原字符串取出子字符串并返回，不改变原字符串
		substr方法用于从原字符串取出子字符串并返回，不改变原字符串,第一个参数是子字符串的开始位置，第二个参数是子字符串的长度
		indexOf()，lastIndexOf()
		trim方法用于去除字符串两端的空格，返回一个新字符串，不改变原字符串
		
		RegExp相关的方法:
			match方法用于确定原字符串是否匹配某个子字符串，返回一个数组，成员为匹配的第一个字符串。如果没有找到匹配，则返回null
			search方法的用法等同于match，但是返回值为匹配的第一个位置。如果没有找到匹配，则返回-1
			replace方法用于替换匹配的子字符串，一般情况下只替换第一个匹配（除非使用带有g修饰符的正则表达式）
			split方法按照给定规则分割字符串，返回一个由分割出来的子字符串组成的数组


6.Array 对象
	构造函数,用它生成新的数组,参数是指成员个数
		var arr = new Array(2);
		arr.length // 2
		不建议使用其作为生成新数组的方法,尽量直接 var arr = [1, 2];
	方法:
		Array.isArray(数组名):用来判断一个值是否为数组。它可以弥补typeof运算符的不足(只会返回其为对象)
		通用对象的6种实例方法
		push()方法:用于在数组的末端添加一个或多个元素，并返回添加新元素后的数组长度。注意，该方法会改变原数组
			var a = [];
			a.push(1) // 1
			a.push('a') // 2
			a.push(true, {}) // 4
			a // [1, 'a', true, {}]
		pop()方法:用于删除数组的最后一个元素，并返回被删除的元素
		join()方法:以参数作为分隔符(参数是字符串)，将所有数组成员组成一个字符串返回。如果不提供参数，默认用逗号分隔
		concat()方法用于多个数组的合并。它将新数组的成员，添加到原数组的尾部，然后返回一个新数组，原数组不变
		shift()方法用于删除数组的第一个元素，并返回该元素
		unshift()方法可以在数组头部添加多个元素
		reverse()方法用于颠倒数组中元素的顺序，返回改变后的数组
		slice()方法用于提取原数组的一部分，返回一个新数组，原数组不变
		splice()方法用于删除原数组的一部分成员，并可以在被删除的位置添加入新的数组成员，返回值是被删除的元素
		sort()方法对数组成员进行排序，默认是按照字典顺序排序
		map()方法对数组的所有成员依次调用一个函数，根据函数结果返回一个新数组
		forEach()方法与map方法很相似，也是遍历数组的所有成员，执行某种操作，但是forEach方法一般不返回值，只用来操作数据。如果需要有返回值，一般使用map方法。
		filter()方法的参数是一个函数，所有数组成员依次执行该函数，返回结果为true的成员组成一个新数组返回。该方法不会改变原数组
		reduce()，reduceRight()
		indexOf()，lastIndexOf()

7.Math对象
	属性:Math对象提供以下一些只读的数学常数
		Math.E：常数e。
		Math.LN2：2的自然对数。
		Math.LN10：10的自然对数。
		Math.LOG2E：以2为底的e的对数。
		Math.LOG10E：以10为底的e的对数。
		Math.PI：常数Pi。
		Math.SQRT1_2：0.5的平方根。
		Math.SQRT2：2的平方根。

	只有静态方法:
		Math.abs()：绝对值
		Math.ceil()：向上取整
		Math.floor()：向下取整
		Math.max()：最大值
		Math.min()：最小值
		Math.pow()：指数运算
		Math.sqrt()：平方根
		Math.log()：自然对数
		Math.exp()：e的指数
		Math.round()：四舍五入
		Math.random()：随机数,无参数返回0,1之间的随机数,有2个参数做界限
		三角函数方法
			Math.sin()：返回参数的正弦
			Math.cos()：返回参数的余弦
			Math.tan()：返回参数的正切
			Math.asin()：返回参数的反正弦（弧度值）
			Math.acos()：返回参数的反余弦（弧度值）
			Math.atan()：返回参数的反正切（弧度值）

8.Date对象(js提供的日期时间接口,能表示1970年1月1日00:00:00前后的各1亿天)
	作为普通函数:直接调用，返回一个代表当前时间的字符串,Date();
	作为构造函数:
		new Date()不加参数,返回当前时间
		new Date()支持各种形式的参数
			字符串日期
			数字表达式,1970到才参数的秒数
	静态方法:
		Date.now方法返回当前距离1970年1月1日 00:00:00 UTC的毫秒数
		Date.parse方法用来解析日期字符串，返回距离1970年1月1日 00:00:00的毫秒数
		Date.UTC方法可以返回UTC时间（世界标准时间）
	实例方法:
		to类：从Date对象返回一个字符串，表示指定的时间。
		get类：获取Date对象的日期和时间。
			getTime()：返回距离1970年1月1日00:00:00的毫秒数，等同于valueOf方法。
			getDate()：返回实例对象对应每个月的几号（从1开始）。
			getDay()：返回星期几，星期日为0，星期一为1，以此类推。
			getYear()：返回距离1900的年数。
			getFullYear()：返回四位的年份。
			getMonth()：返回月份（0表示1月，11表示12月）。
			getHours()：返回小时（0-23）。
			getMilliseconds()：返回毫秒（0-999）。
			getMinutes()：返回分钟（0-59）。
			getSeconds()：返回秒（0-59）。
			getTimezoneOffset()：返回当前时间与UTC的时区差异，以分钟表示，返回结果考虑到了夏令时因素。
		set类：设置Date对象的日期和时间

9.RegExp对象
	新建正则表达式有2种方法:
		var reg=/正则表达式/;	尽量采用这种方法
		var regex = new RegExp('正则表达式','修饰');

	属性:
		一类是修饰符相关，返回一个布尔值，表示对应的修饰符是否设置
			ignoreCase：返回一个布尔值，表示是否设置了i修饰符，该属性只读。
			global：返回一个布尔值，表示是否设置了g修饰符，该属性只读。
			multiline：返回一个布尔值，表示是否设置了m修饰符，该属性只读。
		另一类是与修饰符无关的属性
			lastIndex：返回下一次开始搜索的位置。该属性可读写，但是只在设置了g修饰符时有意义。
			source：返回正则表达式的字符串形式（不包括反斜杠），该属性只读。

	实例方法:(参数为字符串)
		search() 方法 用于检索字符串中指定的子字符串，或检索与正则表达式相匹配子字符串，并返回子串的起始位置。
		replace() 方法 用于在字符串中用一些字符替换另一些字符，或替换一个与正则表达式匹配的子串。
		test() 方法用于检测一个字符串是否匹配某个模式，如果字符串中含有匹配的文本，则返回 true，否则返回 false。
		exec() 方法用于检索字符串中的正则表达式的匹配。该函数返回一个数组，其中存放匹配的结果。如果未找到匹配，则返回值为 null。

		String对象的实例方法:见String对象

	匹配规则:
		http://javascript.ruanyifeng.com/stdlib/regexp.html#toc10
		直接给出字符，就是精确匹配，特殊字符需要 "转义字符\"

		\d可以匹配一个数字
		\w可以匹配一个字母或数字
		.可以匹配任意字符
		\s可以匹配一个空格

		“紧跟在上列字符后面，表示个数”
		*表示任意个字符（包括0个）
		+表示至少一个字符
		?表示0个或1个字符
		{n}表示n个字符
		{n,m}表示n-m个字符

		[] 表示范围，() 表示分隔优先级，{}表示字符个数
		| 或者，^表示开头 ^6，$表示结束 6$
	
10.JSON对象
	{
		"aa":值,
		"bb":值
	}

	键值对应
	访问2种方式:
		整体.aa
		整体["aa"]

	JSON.parse()	用于将一个 JSON 字符串转换为 JavaScript 对象。
	JSON.stringify()	用于将 JavaScript 值转换为 JSON 字符串。
11.console对象
12.属性描述对象

```

``` javaScript
AJAX = 异步 JavaScript 和 XML。
AJAX 是一种用于创建快速动态网页的技术。

GET 还是 POST？
    与 POST 相比，GET 更简单也更快，并且在大部分情况下都能用。
    然而，在以下情况中，请使用 POST 请求：
    无法使用缓存文件（更新服务器上的文件或数据库）
    向服务器发送大量数据（POST 没有数据量限制）
    发送包含未知字符的用户输入时，POST 比 GET 更稳定也更可靠

创建XMLHttpRequest对象 用于在后台与服务器交换数据
            var xmlhttp;
            if (window.XMLHttpRequest){
                xmlhttp=new XMLHttpRequest();
            }
            else{
                xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
            }


向url后添加信息:?
    为了避免由于缓存得到一样的结果，请向 URL 添加一个唯一的 ID：
    xmlhttp.open("GET","/try/ajax/demo_get.php?t=" + Math.random(),true); 
    
post 上传数据,请使用 setRequestHeader() 来添加 HTTP 头。插在open()和send(string)之间
    setRequestHeader(header,value)	
        向请求添加 HTTP 头。
        header: 规定头的名称
        value: 规定头的值
        
        
要是用于AJAX,必须设置为异步async=true 
当使用 async=true 时，请规定在响应处于 onreadystatechange 事件中的就绪状态时执行的函数：
xmlhttp.onreadystatechange=function(){}



获取服务器响应:
如需获得来自服务器的响应，请使用 XMLHttpRequest 对象的 responseText 或 responseXML 属性。
responseText	获得字符串形式的响应数据。
responseXML	    获得 XML 形式的响应数据。

XMLHttpRequest 对象的三个重要的属性：
    onreadystatechange: 存储函数（或函数名），每当 readyState 属性改变时，就会调用该函数。
    readyState: 存有 XMLHttpRequest 的状态。从 0 到 4 发生变化。
                0: 请求未初始化
                1: 服务器连接已建立
                2: 请求已接收
                3: 请求处理中
                4: 请求已完成，且响应已就绪
    status:状态
            200: "OK"
            404: 未找到页面


xmlhttp.onreadystatechange=function(){
      if (xmlhttp.readyState==4 && xmlhttp.status==200){
        需要执行的函数或动作
      }
  }
  
  
  如果您的网站上存在多个 AJAX 任务，那么您应该为创建 XMLHttpRequest 对象编写一个标准的函数，并为每个 AJAX 任务调用该函数。
该函数调用应该包含 URL 以及发生 onreadystatechange 事件时执行的任务（每次调用可能不尽相同）：




<!DOCTYPE html>
<html>
<head>
<script>
var xmlhttp;
function loadXMLDoc(url,cfunc)
{
if (window.XMLHttpRequest)
  {// IE7+, Firefox, Chrome, Opera, Safari 代码
  xmlhttp=new XMLHttpRequest();
  }
else
  {// IE6, IE5 代码
  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xmlhttp.onreadystatechange=cfunc;
xmlhttp.open("GET",url,true);
xmlhttp.send();
}
function myFunction()
{
	loadXMLDoc("/try/ajax/ajax_info.txt",function()
	{
		if (xmlhttp.readyState==4 && xmlhttp.status==200)
		{
			document.getElementById("myDiv").innerHTML=xmlhttp.responseText;
		}
	});
}
</script>
</head>
<body>

<div id="myDiv"><h2>使用 AJAX 修改文本内容</h2></div>
<button type="button" onclick="myFunction()">修改内容</button>

</body>
</html>


jQuery AJAX



1.jQuery load() 方法
    load() 方法从服务器加载数据，并把返回的数据放入被选元素中。
    语法：
    $(selector).load(URL,data,callback);
    (1)必需的 URL 参数规定您希望加载的 URL。
    (2)可选的 data 参数规定与请求一同发送的查询字符串键/值对集合。
        下面的例子把 "demo_test.txt" 文件中 id="p1" 的元素的内容，加载到指定的 <div> 元素中：
            实例
            $("#div1").load("demo_test.txt #p1");
        
    可选的 callback 参数是 load() 方法完成后所执行的函数名称。
        可选的 callback 参数规定当 load() 方法完成后所要允许的回调函数。回调函数可以设置不同的参数：
            responseTxt - 包含调用成功时的结果内容
            statusTXT - 包含调用的状态
            xhr - 包含 XMLHttpRequest 对象
                $("button").click(function(){
                  $("#div1").load("demo_test.txt",function(responseTxt,statusTxt,xhr){
                    if(statusTxt=="success")
                      alert("外部内容加载成功！");
                    if(statusTxt=="error")
                      alert("Error: "+xhr.status+": "+xhr.statusText);
                  });
                });

jQuery $.get() 方法
$.get() 方法通过 HTTP GET 请求从服务器上请求数据。
    语法：
    $.get(URL,callback);
    必需的 URL 参数规定您希望请求的 URL。
    可选的 callback 参数是请求成功后所执行的函数名。
    下面的例子使用 $.get() 方法从服务器上的一个文件中取回数据：
    function(data,status){}

    data必选参数,服务器返回参数,status可选参数,服务器请求状态;



    实例
    $("button").click(function(){
      $.get("demo_test.asp",function(data,status){
        alert("Data: " + data + "\nStatus: " + status);
      });
    });
jQuery $.post() 方法
$.post() 方法通过 HTTP POST 请求从服务器上请求数据。
    语法：
    $.post(URL,data,callback);
    必需的 URL 参数规定您希望请求的 URL。
    可选的 data 参数规定连同请求发送的数据。
    可选的 callback 参数是请求成功后所执行的函数名。
    下面的例子使用 $.post() 连同请求一起发送数据：
    实例
    $("button").click(function(){
      $.post("demo_test_post.asp",
      {
        name:"Donald Duck",
        city:"Duckburg"
      },
      function(data,status){
        alert("Data: " + data + "\nStatus: " + status);
      });
});

```