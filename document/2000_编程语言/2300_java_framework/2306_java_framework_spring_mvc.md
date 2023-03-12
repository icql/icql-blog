---
title: java_framework_spring_mvc
date: 2018-10-23 00:00:00
---

## web集成spring容器
``` java
//web.xml配置 spring监听器,初始化spring容器
<context-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>classpath:spring/applicationContext-*.xml</param-value>
</context-param>
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

## springmvc执行原理
![springmvc执行原理](../../../resource/img/260-1.png)
* 核心前端控制器DispatcherServlet相当于一个中央处理器
* 具体执行流程：用户发送请求--前端控制器（处理请求）--响应请求给用户
	* 1）通过处理器映射器HandlerMapping获取handler和interceptor数组（HandlerExecutionChain）
	* 2）根据handler获取 处理器适配器HandlerAdaptor，使用处理器适配器去执行Handler的方法返回执行结果ModelAndView（这里使用适配器设计模式，下面源码分析会细述）
	* 3）通过视图解析器ViewResolver解析ModelAndView里的逻辑视图View得到物理视图（例如jsp的具体地址）
	* 4）渲染视图并返回

## 使用细节
``` java
//bean
@Controller

//url映射
@RequestMapping
@GetMaping
@PostMaping
@PutMapping
@DeleteMapping

//参数
@PathVariable 路径
@RequestParam 单个参数（application/x-www-form-urlencoded）
@RequestBody 对象（application/json / application/xml / application/x-www-form-urlencoded）

@RequestHeader
@CookieValue
//@ModelAttribute 不常用

//参数没有注解时，
若要绑定的对象是简单类型： 调用@RequestParam来处理的
若要绑定的对象是复杂类型： 调用@ModelAttribute来处理的

//文件上传
<!-- 定义文件上传解析器 -->
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
	<!-- 设定默认编码 -->
	<property name="defaultEncoding" value="UTF-8"></property>
	<!-- 设定文件上传的最大值500MB，500*1024*1024 -->
	<property name="maxUploadSize" value="524288000"></property>
</bean>
//方法参数
MultipartFile file

//转发和重定向
return "forward：/user/list";
return "redirect：/user/list";

//Hibernate Validator
//Hibernate Validator 是 Bean Validation 的参考实现，提供了JSR 303规范中所有内置constraint的实现，除此之外还有一些附加的constraint。在日常开发中，Hibernate Validator经常用来验证bean的字段，基于注解，方便快捷高效。

//Bean Validation 中内置的 constraint
@Null	被注释的元素必须为 null
@NotNull	被注释的元素必须不为 null
@AssertTrue	被注释的元素必须为 true
@AssertFalse	被注释的元素必须为 false
@Min(value)	被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@Max(value)	被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@DecimalMin(value)	被注释的元素必须是一个数字，其值必须大于等于指定的最小值
@DecimalMax(value)	被注释的元素必须是一个数字，其值必须小于等于指定的最大值
@Size(max, min)	被注释的元素的大小必须在指定的范围内
@Digits (integer, fraction)	被注释的元素必须是一个数字，其值必须在可接受的范围内
@Past	被注释的元素必须是一个过去的日期
@Future	被注释的元素必须是一个将来的日期
@Pattern(value)	被注释的元素必须符合指定的正则表达式

//Hibernate Validator 附加的 constraint
@Email	被注释的元素必须是电子邮箱地址
@Length(min=, max=)	被注释的字符串的大小必须在指定的范围内
@NotEmpty	被注释的字符串的必须非空
@Range(min=, max=)	被注释的元素必须在合适的范围内


//springmvc中如何使用
//校验单个参数
注意：一定要在Controller上加注解 @Validated，方法的参数直接使用上述注解匹配
@GetMapping("/test")
public void test(@Length(max = 5, message = "参数值长度最大为5") String test1, @Length(max = 4, message = "参数值长度最大为4") String test2) {}

//校验对象
@PostMapping("/test")
public String test(@Validated @RequestBody User user) {}
```

## springmvc源码分析
* 1）ApplicationContext初始化时建立所有url和controller类的对应关系（用Map保存）
	* （1）入口类为 ApplicationObjectSupport 的 setApplicationContext 方法
，setApplicationContext 方法中核心部分就是初始化容器 initApplicationContext(context)，子类 AbstractDetectingUrlHandlerMapping 实现了该方法,所以我们直接看子类中的初始化容器方法
	``` java
	public void initApplicationContext() throws ApplicationContextException {
			super.initApplicationContext();
			detectHandlers();
	}
	//建立当前ApplicationContext中的所有controller和url的对应关系
	protected void detectHandlers() throws BeansException {
		if (logger.isDebugEnabled()) {
			logger.debug("Looking for URL mappings in application context: " + getApplicationContext());
		}
	　　　　 // 获取ApplicationContext容器中所有bean的Name
		String[] beanNames = (this.detectHandlersInAncestorContexts ?
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(getApplicationContext(), Object.class) :
				getApplicationContext().getBeanNamesForType(Object.class));

		// 遍历beanNames,并找到这些bean对应的url
		for (String beanName : beanNames) {
	　　　　　　 // 找bean上的所有url(controller上的url+方法上的url),该方法由对应的子类实现
			String[] urls = determineUrlsForHandler(beanName);
			if (!ObjectUtils.isEmpty(urls)) {
				// 保存urls和beanName的对应关系,put it to Map<urls,beanName>,该方法在父类AbstractUrlHandlerMapping中实现
				registerHandler(urls, beanName);
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Rejected bean name '" + beanName + "': no URL paths identified");
				}
			}
		}
	}
	/** 获取controller中所有方法的url,由子类实现,典型的模板模式，不同的HandlerMapping有不同的实现，例如xml配置和注解配置 **/
	protected abstract String[] determineUrlsForHandler(String beanName);
	```

	* （2）determineUrlsForHandler(String beanName)方法的作用是获取每个controller中的url，不同的子类有不同的实现,这是一个典型的模板设计模式，因为开发中我们用的最多的就是用注解来配置controller中的url,DefaultAnnotationHandlerMapping是AbstractDetectingUrlHandlerMapping的子类,处理注解形式的url映射

* 2）前端控制器处理请求过程
	* （1）入口为DispatcherServlet的核心方法为doService(),doService()中的核心逻辑由doDispatch()实现,我们查看doDispatch()的源代码
	``` java
	// 中央控制器,控制请求的转发
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		int interceptorIndex = -1;

		try {
			ModelAndView mv;
			boolean errorView = false;
			try {
				// 1.检查是否是文件上传的请求
				processedRequest = checkMultipart(request);

				// 2.取得处理当前请求的controller,这里也称为hanlder,处理器,第一个步骤的意义就在这里体现了.这里并不是直接返回controller,而是返回的HandlerExecutionChain请求处理器链对象,该对象封装了handler和interceptors.
				mappedHandler = getHandler(processedRequest, false);
				// 如果handler为空,则返回404
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					noHandlerFound(processedRequest, response);
					return;
				}
				//3. 获取处理request的处理器适配器handler adapter 
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
				// 处理 last-modified 请求头
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (logger.isDebugEnabled()) {
						String requestUri = urlPathHelper.getRequestUri(request);
						logger.debug("Last-Modified value for [" + requestUri + "] is: " + lastModified);
					}
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}

				// 4.拦截器的预处理方法
				HandlerInterceptor[] interceptors = mappedHandler.getInterceptors();
				if (interceptors != null) {
					for (int i = 0; i < interceptors.length; i++) {
						HandlerInterceptor interceptor = interceptors[i];
						if (!interceptor.preHandle(processedRequest, response, mappedHandler.getHandler())) {
							triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, null);
							return;
						}
						interceptorIndex = i;
					}
				}

				// 5.实际的处理器处理请求,返回结果视图对象
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

				// 结果视图对象的处理
				if (mv != null && !mv.hasView()) {
					mv.setViewName(getDefaultViewName(request));
				}

				// 6.拦截器的后处理方法
				if (interceptors != null) {
					for (int i = interceptors.length - 1; i >= 0; i--) {
						HandlerInterceptor interceptor = interceptors[i];
						interceptor.postHandle(processedRequest, response, mappedHandler.getHandler(), mv);
					}
				}
			}
			catch (ModelAndViewDefiningException ex) {
				logger.debug("ModelAndViewDefiningException encountered", ex);
				mv = ex.getModelAndView();
			}
			catch (Exception ex) {
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				mv = processHandlerException(processedRequest, response, handler, ex);
				errorView = (mv != null);
			}

			
			if (mv != null && !mv.wasCleared()) {
				render(mv, processedRequest, response);
				if (errorView) {
					WebUtils.clearErrorRequestAttributes(request);
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + getServletName() +
							"': assuming HandlerAdapter completed request handling");
				}
			}

			// 请求成功响应之后的方法
			triggerAfterCompletion(mappedHandler, interceptorIndex, processedRequest, response, null);
		}
	}
	```
	* （2）上面第5步的调用的是处理器适配器HandlerAdapter接口的handle(HttpServletRequest request, HttpServletResponse response, Object handler)方法，这里使用的是适配器设计模式，不同的Handler有各自的HandlerAdapter，因此父接口调用子类实现的handle()方法处理，经常用的Controller注解是调用的AnnotationMethodHandlerAdapter适配器的handle()方法，反射调用处理请求的方法,返回结果视图
		* 上述执行过程中，比较复杂的是参数值绑定，springmvc中提供两种request参数到方法中参数的绑定方式:
			* 通过注解进行绑定,@RequestParam
			* 通过参数名称进行绑定.
	　　* 使用注解进行绑定,我们只要在方法参数前面声明@RequestParam("a"),就可以将request中参数a的值绑定到方法的该参数上.使用参数名称进行绑定的前提是必须要获取方法中参数的名称,Java反射只提供了获取方法的参数的类型,并没有提供获取参数名称的方法.springmvc解决这个问题的方法是用asm框架读取字节码文件,来获取方法的参数名称.asm框架是一个字节码操作框架,关于asm更多介绍可以参考它的官网.个人建议,使用注解来完成参数绑定,这样就可以省去asm框架的读取字节码的操作

* 3）springmvc使用注意点
	* controller如果能保持单例,尽量使用单例,这样可以减少创建对象和回收对象的开销.也就是说,如果controller的类变量和实例变量可以以方法形参声明的尽量以方法的形参声明,不要以类变量和实例变量声明,这样可以避免线程安全问题.
	* 处理request的方法中的形参务必加上@RequestParam注解,这样可以避免springmvc使用asm框架读取class文件获取方法参数名的过程.即便springmvc对读取出的方法参数名进行了缓存,如果不要读取class文件当然是更加好

## 参考
* https://www.cnblogs.com/heavenyes/p/3905844.html
* https://blog.csdn.net/u010288264/article/details/53835185