---
title: java_ee
date: 2018-10-25 00:00:00
---

哈哈，待重新学习整理

## java ee 核心设计模式
* web浏览器 —— Servlet控制层（不需要业务逻辑） —— jsp页面 —— html doc —— web浏览器
* web浏览器 —— Servlet控制层（需要业务逻辑） —— JavaBean（简单java类）、ejb、数据库 —— jsp页面 —— html doc —— web浏览器
* 启动的顺序为listener->Filter->servlet：简单记为：理(Listener)发(Filter)师(servlet)
* Listener生命周期：一直从程序启动到程序停止运行
* Filter生命周期：程序启动调用Filter的init()方法(永远只调用一次,具体看启动日志)，程序停止调用Filter的destroy()方法(永远只调用一次，具体看关闭日志)
* Servlet生命周期：程序第一次访问，会调用servlet的init()方法初始化(只执行一次，具体看日志)，每次程序执行都会根据请求调用doGet()或者doPost()方法，程序停止调用destory()方法

## maven安装jar到本地仓库
``` bash
//一定要在idea中的控制台中输入
mvn install:install-file -DgroupId=com.aliyun -DartifactId=aliyun-java-sdk-core -Dversion=3.3.1 -Dpackaging=jar -Dfile=test.jar
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 -Dversion=10.2.0.4.0 -Dpackaging=jar -Dfile=ojdbc14-10.2.0.4.0.jar
```

## xml
	* 文档结构
		* 前导区<?xml version="1.0" encoding="utf-8"?>
			* version，xml版本
			* encoding，编码
			* standalone，此xml文件是否独立运行
		* 数据区
			* 必须只有一个根元素
			* 每个元素必须完结，要求严格
			* 区分大小写
			* 可自定义属性
			* 特殊字符显示 &：&amp; <: &lt; >: &gt; ":&quot; ':&apos;
			* <![CDATA[不希望被解析的部分]]>
		* 如果要对xml文件的元素或属性进行约束，需要使用DTD和Schhema技术
	* xml解析
		* dom解析接口
		* sax解析接口
		* dom4j包
```java
/**
* 
*/
package com.icql.demo;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
* @author A6924
*
*/
public class TestDemo {

    /**
    * @param args
    */
    public static void main(String[] args) {
        Document doc = DocumentHelper.createDocument();
        Element addresslist = doc.addElement("addresslist");
        Element linkman = addresslist.addElement("linkman");
        Element name = linkman.addElement("name");
        Element email = linkman.addElement("email");
        name.setText("icql");
        email.setText("icql618@qq.com");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        try {
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File("E:" + File.separator + "1.xml")));
            writer.write(doc);
            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

        SAXReader reader = new SAXReader();
        Document docRead = null;
        try {
            File file = new File("E:" + File.separator + "1.xml");
            docRead = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        
        Element root = docRead.getRootElement();
        Iterator iter = root.elementIterator();
        while (iter.hasNext()) {
            Element linkman1 = (Element) iter.next();
            System.out.println(linkman1.elementText("name"));
        }

    }

}
```

## tomcat
	* 配置
		```java
		//环境变量
		Path：加入 tomcat/bin
		Using CATALINA_BASE:   "D:\apache-tomcat-7.0.52"
		Using CATALINA_HOME:   "D:\apache-tomcat-7.0.52"
		Using CATALINA_TMPDIR: "D:\apache-tomcat-7.0.52\temp"
		Using JAVA_HOME:       "C:\Program Files\Java\jdk1.8.0_121"
		
		//修改默认端口号 conf/server.xml文件中，修改 8080
		<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443">

		//web应用配置 conf/server.xml文件中 Server-Service-Engine-Host节点下加入
						<Context path="/test" docBase='C:\Users\a6924.GOLDDRAGON.COM\Desktop\test' />
					</Host>
				</Engine>
			</Service>
		</Server>
		```
	* http 状态码
		* 2xx 请求成功
		* 3xx 重定向
		* 4xx （403禁止，404找不到指定资源）
		* 5xx （服务器中出现的错误，500服务器内部错误）

## jsp
	* 1.jsp语法
		* 注释（html注释<!-- -->，jsp注释<%-- --%>，java注释）
		* Scriptlet
			* <%%>：可直接在里面写 java传统方法 里面的代码
			* <%!%>：可在里面 定义类、方法、全局变量等；尽量不要使用
			* <%=%>：输出一个变量或一个具体内容，表达式输出；尽量使用这种输出方式
		* Scriptlet标签：<jsp:scriptlet>中间写java代码</jsp:scriptlet>相当于<%%>，只是比较美观

		* page指令 <%@ page langage="java" contentType="text/html;charset=GBK" %>
			* contentType：设置页面MIME和编码，mime指的是文件后缀名及类型，charset编码指的是服务器发送给客户端的内容编码
			* pageEncoding：设定jsp文件本身的编码
			* 编码解释：若pageEncoding存在，则jsp的编码由pageEncoding决定，否则将由contentType中的charset属性决定；若两者都不存在，则将使用ISO-8859-1的编码方式，在jsp中，所有内容都要经过两次编码操作，第一阶段会使用pageEncoding编码，第二阶段会使用utf-8编码，第三阶段就是tomcat生成的网页，此时使用的才是 contentType；一般开发，jsp页面若只需要按照text/html显示，则设置pageEncoding设置编码即可
			* errorPag：发生错误跳转至此属性设置的页面，可每个页面单独设置，也可全局设置，在web.xml文件中配置；若想要显示的error.jsp也被服务器认为出现了错误，可以在error.jsp中单独设置<% response.setStatus(200); %>让服务器始终认为此页面没有错误
			* import：可以利用此属性在jsp页面导入java开发包，此属性可以多次出现，其余属性不可以，例如import="java.sql.*"
			* autoFlush：
			* buffer:
			* extends:
			* info:
			* isErrorPage:
			* isThreadSafe:
			* language:
			* session:
		* 包含指令
			* 静态包含<%@include file=""%>：先将文件内容包含进来，在一起处理，这样如果被包含的文件里定义的变量名和当前文件里面定义的变量名重复，会报错
			* 动态包含<jsp:include page="文件路径|<%=表达式%>" flush="true|flase" />：先将要包含进来的文件处理，将处理后的静态页面再包含进来
				```java
				//传参
				<jsp:include page="receive_param.jsp">
					<jsp:param name="name" value"<%=username%>" />
					<jsp:param name="info" value"icql" />
				</jsp:include>
				//接收
				<%=request.getParameter("name")%>
				<%=request.getParameter("info")%>
				```
		* 跳转指令
			* <jsp:forward page="文件路径 | <%=表达式%>"/>也可以传参
			* 属于服务器跳转

	* 2.jsp内置对象（由web容器实例化，可直接使用）
		* 1、application（javax.servlet.ServeletContext）
			* 可设置属性，生命周期在同一个web应用程序中，所有用户共享，设置过多影响服务器性能
				* application.serAttribute("","")
				* application.getAttribute("")
			* 其他方法
				* getRealPath(String path)
				* getContextPath()
				* getAttributeNames()

		* 2、session（javax.servlet.http.HttpSession）
			* 可设置属性，生命周期在同一个web应用程序中，每个用户有一个自己的sessinID，不同的用户由不同的session
				* session.serAttribute("","")
				* session.getAttribute("")
			* 其他常用方法
				* getId() 获取Session Id
				* getCreateTime() 获取session创建时间
				* getLastAccessdTime() 取得session最后一次操作时间，与上一个时间相减以获得用户在此停留的时间
				* isNew() 是否新的session用户
				* inValidate() 让session失效
				* getAttributeNames()

			* session 过期方式
				* 1、手动调用 inValidate()
				* 2、超过了配置的session超时时间，默认30分钟，与服务器无交互<session-config><session-timeout>5</session-timeout></session-config>
				* 3、用户关掉了浏览器窗口，保存sessionId的cookie默认不设时间的话就会直接销毁，从而重新打开浏览器重新请求的时候，服务器会当成新用户重新创建sessino

		* 3、request（javax.servlet.http.HttpServletRequest）
			* 可设置属性，生命周期只在一次请求中，使用jsp:forward跳转属于服务器跳转，在此次请求当中，若用a标签直接换了新url去请求，属于新的请求，则不生效
				* request.serAttribute("","")
				* request.getAttribute("")
			* javax.servlet.http.HttpServletRequest extends javax.servlet.http.ServletRequest
			* request常用方法
				* setCharacterEncoding()设置编码
				* getParamterNames()
				* getParameter()接收单个参数
				* getParameterValues()接收同名参数数组，例如复选框，多个相同的name
				```java
				//一般设置多个相同name为"**"开头
				Enumeration enu = request.getParameterNames();
				while(enu.hasMoreElements()){
					String paramName = (String)enu.nextElement();
					if(paramName.startWith("**")){
						String paramValue[] = request.getParameterValues(paramName);
					}else{
						String paramValue = request.getParameter(paramName);
					}
				}
				```
				* getHeaderNames()
				* getHeader()
				* getRemoteAddr()
				* getMethod()
				* getCookies()
				* 提交方式get,set
			* Http Header参数https://blog.csdn.net/alanlzz/article/details/72846718

		* 4、pageCotext（javax.servlet.jsp.PageContext）jsp的页面容器
			* 可设置属性，生命周期只在本jsp页面
				* pageContext.serAttribute("","")
				* pageContext.getAttribute("")
			* 诸多方法可以取得以下的jsp内置对象
				* forward(String relativeUrlPath)页面跳转

		* response（javax.servlet.http.HttpServletResponse）
			* response常用方法
				* setHeader()设置响应头信息
				* sendRedirect()重定向，浏览器会发出两次请求，属于客户端跳转，与<jsp:forward>的服务器端跳转不同
				* addCookie()
					* Cookie类常用方法
						* public Cookie(String name,String value)
						* public String getName()
						* public String getValue()
						* public void setMaxAge(int expiry) 设置Cookie保存时间

		* config（javax.servlet.ServletConfig）
			* getInitParameter(String name)
			* getInitParameterNames()
			* 在web.xml中设置参数
				```java
				<web-app>
					<servelet>
						<init-param>
							<param-name>  </param-name>
							<param-value>  </param-value>
						</init-param>
						<init-param>
							<param-name>  </param-name>
							<param-value>   </param-value>
						</init-param>
					</servelet>
				</web-app>
				```
		* out（javax.servlet.jsp.JspWriter）
			* 页面的输出操作，用的较少
			* println()
			* print()
		* page（java.lang.Object），指向jsp页面本身，类似于 this
		* exception（java.lang.Throwable）

	* 3.javabean
		* jsp导入javabean：
			* 使用page指令导入包，然后手动实例化
			* <jsp:useBean id="实例化对象名称" scope="保存范围" class="包.类名称" />
		* <jsp:setProperty name="" property="" value="" /> 设置指定属性
		* <jsp:getProperty name="" property="" /> 获得指定属性
		* javabean保存范围：page request session application
		* 删除javabean，使用上述4种的属性移除操作，例如 page.removeAttribute(javabean名称)

	* 4.文件上传
		* smartupload上传组件
			```java
			//单文件表单 <input type="file" name="pic" />
			SmartUpload smart = new SmartUpload();
			smart.initialize(pageContext);
			smart.upload();
			smart.save("upload");
			//多表单（既有文本又有文件），<form enctype="multipart/form-data"> 因为form对表单进行了二进制封装，所以使用request.getParameter("")无法获取
			SmartUpload smart = new SmartUpload();
			smart.initialize(pageContext);
			smart.upload();
			String name = smart.getRequest().getParameter("");
			smart.save("upload");
			//为上传文件重命名
			martUpload smart = new SmartUpload();
			smart.initialize(pageContext);
			smart.upload();
			smart.getFiles().getFile(0).saveAs("文件名");
			```
		* FileUpload上传组件

## servlet: extends HttpServlet extends GenericServlet
	* 生命周期：加载（当web容器启动或第一次调用这个servlet时，web容器实例化一个servlet对象）-初始化（init方法）-处理服务（service方法，由service区分get，post继而转到doGet或doPost）-销毁（destory方法）-卸载
	* init(ServletConfig config)方法，使用web.xml中的配置初始化servlet，在init()方法中使用 config.getParameter("")获取
	```java
		<servlet>
			<servlet-name>HelloServlet</servlet-name>
			<servlet-class>com.icql.servletdemo.HelloServlet</servlet-class>
			<init-param>
				<param-name>name</param-name>
				<param-value>icql</param-value>
			</init-param>
			<init-param>
				<param-name>age</param-name>
				<param-value>24</param-value>
			</init-param>
		</servlet>
	```

	* servlet中获取其他内置对象
		* request
		* response
		* session：request.getSession()
		* servletContext:使用GenericServlet中定义的方法getServletContext()

	* servlet跳转
		* 客户端跳转
			* response.sendRedirect("")，由于是客户端跳转，只能传递session范围内的值，不能传递request的值，相当于重新发送了一次请求，但可以先把request的值取出来
		* 服务端跳转
			* 需要RequestDispatcher接口实例的forward()方法
			* request.getRequestDispatcher("").forward();
	
	* web开发模式
		* Model1（类似于jsp+DAO）
			* 完全jsp开发
			* 显示操作写入jsp页面，业务层写成 javabean形式
		* Model2（Model+View+Controller）
			* 客户端-request-servlet(利用javabean操作数据库)-jsp-response-客户端
			* mvc设计模式
				* 显示层View:jsp页面负责接收Servlet传递过来的内容，并且利用javabean将内容显示为html界面呈现给客户端
				* 控制层Controller：主要是Servlet负责接收所有用户的请求参数，判断参数是否合法，根据请求类型调用javabean执行操作，并将处理结果交由显示层显示
				* 模型层Model：完成一个独立的业务组件，一般都是以javabean或者ejb的形式定义的
				* 所有数据传递都是用request进行传递，其他3种传递范围或性能不合适
			* jsp页面一般最好只出现
				* 接收属性：Servlet传来的属性
				* 判断语句：判断jsp中的属性是否存在
				* 输出内容：迭代或者VO输出，jsp页面只允许导入的包是 java.util包
	
	* 过滤器：需实现 javax.servlet.Filter接口
		* 过滤器链
		* 需要重写的方法
			* public void init(FilterConfig filterConfig)初始化，读取配置文件，在服务器启动时执行
			* public void doFilter(ServletRequest request,SevletResponse response,FilterChain chain)执行过滤动作，请求过滤的地址时执行，注意一个页面调用两次是因为favicon.ico也需要发送请求，如果在方法里需要使用session等对象，需要将ServletRequest 向下转型为 HttpServletRequest
			* public void destroy()过滤器销毁，在关闭服务器时执行
		* web.xml配置同servlet，多个过滤器，按照配置顺序执行
		```java
			<filter>
				<filter-name>DemoFilter</filter-name>
				<filter-class>com.icql.demo.filter.DemoFilter</filter-class>
				<init-param>
					<param-name>charset</param-name>
					<param-value>UTF-8</param-value>
				</init-param>
			</filter>
			<filter-mapping>
				<filter-name>DemoFilter</filter-name>
				<url-pattern>/login/</url-pattern>
			</filter-mapping>
		```
	
	* 监听器：Listener

	``` java
	//可以对application，session，request 3种操作进行监听
	<listener>
		<listener-class>com.icql.demo.listener.DemoListener</listener-class>
	</listener>
	//实例：利用application和session统计在线人员

	//application（javax.servlet.ServeletContext）
		* 上下文状态监听 ServletContextListener接口
			* public void contextInitialized(ServletContextEvent sce)容器启动时触发
			* public void contextDestroyed(ServletContextEvent sce)容器销毁时触发
		* 属性监听 ServletContextAttributeListener接口
			* public void attributeAdded(ServletContextAttributeEvent scab)增加属性时触发
			* public void attributeRemoved(ServletContextAttributeEvent scab)移除属性时触发
			* public void attributeReplaced(ServletContextAttributeEvent scab)替换属性时触发
	//session
		* 状态监听 HttpSessionListener接口
			* sessionCreated()
			* sessionDestroyed()
		* 属性监听 HttpSessionAttributeListener接口，需配置到 配置文件
			* public void attributeAdded()
			* public void attributeRemoved()
			* public void attributeReplaced()
		* 属性监听 HttpSessionBindingListender接口，不需要配置，直接使用
			* 直接在需要使用的类，让类直接实现 此接口
			* public void valueBound(HttpSessinoBinddingEvent event)绑定对象到session时触发
			* public void valueUnbound(HttpSessinoBinddingEvent event)从session移除对象时触发
	
	//request
		* 状态监听 ServletRequestListener接口
			* requestInitialized()
			* requestDestroyed()
		* 属性监听 ServletRequestAttributeListener接口
			* public void attributeAdded()
			* public void attributeRemoved()
			* public void attributeReplaced()
	```

## jsp标签
	* 1.jsp标签编程
		* 自定义标签
			* 1、src中定义类：需要直接继承 javax.servlet.jsp.tagext.TagSupport 类，重写doStartTag()等方法，若标签需要属性，则必须设置字段，且有getter和setter方法，此种字段就是标签的属性
			* 2、在WEB-INF中添加 tag描述文件.tld
			* 3、在jsp页面中使用时 需要先导入描述文件 <%@taglib prefix="i" uri="/WEB-INF/hellotag.tld" %>，prefix代表前缀，使用时<前缀:标签名>，其中 uri可以在 web.xml 中 定义映射，方便书写 <jsp-config><taglib><taglib-uri>自定义缩写名</taglib-uri><taglib-location>描述文件路径</taglib-location></taglib></jsp-config>
			
		```java
		//1、tag 类
		package com.icql.test.tag;

		import javax.servlet.jsp.JspException;
		import javax.servlet.jsp.JspWriter;
		import javax.servlet.jsp.tagext.TagSupport;

		public class HelloTag extends TagSupport {
			@Override
			public int doStartTag() throws JspException {
				JspWriter out = super.pageContext.getOut();
				try{
					out.println("<h1>Hello World!</h1>");
				}catch (Exception e){
					e.printStackTrace();
				}

				return TagSupport.SKIP_BODY;//没有标签体
			}
		}

		package com.icql.test.tag;

		import javax.servlet.jsp.JspException;
		import javax.servlet.jsp.tagext.TagSupport;
		import java.text.SimpleDateFormat;
		import java.util.Date;

		public class DateFormatTag extends TagSupport {
			private String format;

			@Override
			public int doStartTag() throws JspException {
				SimpleDateFormat sdf =new SimpleDateFormat(this.format);
				try{
					super.pageContext.getOut().write(sdf.format(new Date()));
				}catch (Exception e){
					e.printStackTrace();
				}

				return TagSupport.SKIP_BODY;
			}

			public String getFormat() {
				return format;
			}

			public void setFormat(String format) {
				this.format = format;
			}
		}

		//2、tag描述文件.tld
		<?xml version="1.0" encoding="ISO-8859-1"?>

		<taglib xmlns="http://java.sun.com/xml/ns/javaee"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd"
				version="2.1">

			<tlib-version>1.0</tlib-version>
			<short-name>icql-tag</short-name>
			<uri>http://mycompany.com</uri>
			<tag>
				<name>hello</name>
				<tag-class>com.icql.test.tag.HelloTag</tag-class>
				<body-content>empty</body-content>
			</tag>
			<tag>
				<name>date</name>
				<tag-class>com.icql.test.tag.DateFormatTag</tag-class>
				<body-content>empty</body-content>
				<attribute>
					<name>format</name>
					<required>true</required>
					<rtexprvalue>true</rtexprvalue><!--标签内支持<%=%>表达式-->
				</attribute>
			</tag>


			<!-- Invoke 'Generate' action to add tags or functions -->

		</taglib>

		//3、jsp使用
		<%--
		Created by IntelliJ IDEA.
		User: A6924
		Date: 2018/5/14
		Time: 14:11
		To change this template use File | Settings | File Templates.
		--%>
		<%@ page contentType="text/html;charset=UTF-8" language="java" %>
		<%@taglib prefix="i" uri="icql-tag" %>
		<html>
		<head>
			<title>Title</title>
		</head>
		<body>
		<h1>test</h1>
		<h2><i:hello /></h2>
		<h2><i:date format="yyyy-MM-dd" /></h2>
		</body>
		</html>

		```

		* TagSupport类
			* implements IterationTag extends Tag
			* 重要方法
				* doStartTag()标签开始时执行
					* 返回SKIP_BODY，则忽略标签体内容，直接doEndTag
					* 返回EVAL_BODY_INCLUDE，表示执行标签体内容
				* doAfterBody()，是IterationTag 和 Tag 接口的主要差别，用来重复执行标签体内容
					* 返回SKIP_BODY，执行doEndTag
					* 返回EVAL_BODY_AGAIN，表示重复执行力标签体内容，会重复调用doAfterBody，直到返回SKIP_BODY
				* doEndTag()
					* 返回SKIP_PAGE表示jsp页面应该立刻停止响应，并将所有的输出即刻传回浏览器
					* 返回EVLAL_PAGE表示jsp可以正常的运行完毕
				* release()将产生或是或得的资源全部释放
		* 迭代标签
		* BodyTagSupport
		* TagExtraInfo和VariableInfo
		* jsp2.0之后简化自带一标签开发，简单标签SimpleTagSupport，只需要重写doTag()方法，即可实现以上所有的标签开发
		* 标签动态属性，Dynamicattributes接口

	* 2.jsp标准标签库 JSTL（JSP Standard Tag Library）
		* jsp引入 <%@taglib prefix="i" uri="web.xml中配置的.td名称" %>
		* 核心标签库
			* 输出<c:out value="" default="" escapeXml="true|false" /> 自闭合 或者 <c:out>默认值</c:out> escapeXml 是否转换字符串，将特殊字符转为html转义字符，默认为 true
			* 设置属性<c:set var="属性名称" value="属性内容" scope="范围，默认page" />自闭合 或者有标签体，标签体代表 属性内容
				* 还可以利用此标签设置 javabean 的属性，<c:set target="javabean对象名称" property="指定的属性名称" value="值" />
			* 删除属性<c:remove var="删除的属性名称" scope="范围" />
			* 异常处理<c:catch var="用来保存异常信息的属性">有可能发生异常的语句</c:catch>
			* if标签 <c:if test="判断条件" var="用来存储判断结果" scope="范围" />
				* 有标签体，标签体 用于满足条件时执行的语句
			* 多个条件的判断 
				```java
				<c：choose>
					<c:when test="判断条件1">语句1</c:when>
					<c:when test="判断条件2">语句2</c:when>
					<c:otherwise>都不满足时执行的语句3</c:otherwise>
				</c：choose>
				```
			* 迭代标签 <c:forEach items="要输出的集合对象或数组对象名" var="相当于item" begin="开始输出位置，默认0" end="结束输出位置，默认最后" step="步长">要输出的内容</c:foreach>

			* 字符串分割迭代输出<c:forTokens items="输出的字符串" delims="字符串分割符" var="相当于item" varStatus="存放当前对象的相关信息" begin="" end="" step="" >要输出的内容</c:forTokens>

			* 导入页面标签 <c:import url="包含地址的URL" context="" var="存储导入的文件内容" scope="var保存的范围" charEncoding="定义的字符编码" varReader="存储导入的文件内容，以Reader类型存入" />
				* 可以向 导入的页面 传递参数，包含在其中，<c:param name="参数名" value="参数值"/>，导入的页面 直接使用 ${param.参数名} 接收
			
			* 处理url标签 <c:url value="操作的url" var="保存新url字符串名" context="" scope="var范围"><c:param name="" value="" /></c:url>

			* 客户端跳转标签 <c:redirect url="跳转的地址" context=""><c:param name="" value="" /></c:redirect>

	* 3.国际化标签库
		* 国际化标签
			* <fmt:setLocale value="地区的编码 java.util.Locale类" >设置全局的地区代码
			* <fmt:requestEncoding value="字符编码">
			* 读取.properties资源文件
		* 数字格式化标签 <fmt:formatNumber>
		* 日期格式化标签 <fmt:formatDate>
		* 设置时区 <fmt:setTimeZone>

	* 4.sql标签库（不建议使用）

	* 5.xml标签库
		```java
		<x:parse var="保存解析过后的xml对象" varDom="使用DOM解析的XMl文件对象" doc="xml地址" scope="默认page" scopeDom="Dom对象的范围">
			<x:out select="Xpath路径" esacpeXml="true|false，是否转换字符串" />
		</x:parse>

		<x:set>
		<x:if>
		<x:choose> <x:when> <x:otherwise>
		<x:foreach>	
		```

	* 6.函数标签库：大部分是字符串的方法

	* 7.jsp表达式语言
			* 表达式语言内置对象
				* 访问4种属性范围
					* pageScope ${pageScope.属性}
					* requestScope ${requestScope.属性}
					* sessionScope ${sessionScope.属性}
					* applicationScope ${applicationScope.属性}
				* 调用jsp内置对象
					* ${pageContext.session}，${pageContext.request}等
				* 接收请求参数，与 request.getParameter()类似
					* ${param.参数名称} 一个参数
					* ${paramValues.参数名称}一组参数，html页面中name相同的一组表单
				* 其他
					* header，headerValues，cookie，initParam（配置的初始化参数）
			* 集合操作
				* 一般来说都是使用的requestScope里的集合属性，所以直接使用 ${集合名[0]}，当然也可以使用其他属性范围，支持 Collection的所有子接口
			* 表达式支持运算符、三目运算符等，因为本身就是表达式