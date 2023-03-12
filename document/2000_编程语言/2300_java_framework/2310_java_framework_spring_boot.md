---
title: java_framework_spring_boot
date: 2020-03-27 00:00:00
---

## springboot

https://spring.io/projects/spring-boot


<br/>
<hr/>

## ConfigurationClassPostProcessor（扫描注册bean）

org.springframework.context.annotation.ConfigurationClassPostProcessor
扫描注册bean的核心后置处理器，是一个 beanFactoryPostProcessor

**继承关系：**
ConfigurationClassPostProcessor -> BeanDefinitionRegistryPostProcessor -> BeanFactoryPostProcessor

### 1）注册时机

``` java
//AnnotationConfigApplicationContext（注解方式的ApplicationContext）
//在构造方法中创建了AnnotatedBeanDefinitionReader
//AnnotatedBeanDefinitionReader 的构造方法中
//注册注解配置类的处理器
//其中有ConfigurationClassPostProcessor


public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	private final AnnotatedBeanDefinitionReader reader;

	private final ClassPathBeanDefinitionScanner scanner;

	public AnnotationConfigApplicationContext() {
        //注意此处
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}
}

//AnnotatedBeanDefinitionReader
//构造方法中调用
AnnotationConfigUtils.registerAnnotationConfigProcessors()

//继续调用AnnotationConfigUtils#registerAnnotationConfigProcessors
public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
        BeanDefinitionRegistry registry, @Nullable Object source) {
    //...
    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        //在这里注册 ConfigurationClassPostProcessor
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }
    //...
}


```


### 2）执行时机
在ioc源码分析中，ioc容器启动时，主流程方法 refresh 中调用了 invokeBeanFactoryPostProcessors 方法

``` java
//invokeBeanFactoryPostProcessors 方法层层调用
//PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors

private static void invokeBeanFactoryPostProcessors(
        Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
    //在这里执行每个 BeanFactoryPostProcessor
    for (BeanFactoryPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessBeanFactory(beanFactory);
    }
}
```


### 3）作用分析

注册以下注解的bean
>@Configuration
>@Component
>@ComponentScan
>@Import
>@ImportResource
>@Bean

``` java
//ConfigurationClassPostProcessor实现BeanFactoryProcessor的方法
@Override
public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    int factoryId = System.identityHashCode(beanFactory);
    if (this.factoriesPostProcessed.contains(factoryId)) {
        throw new IllegalStateException(
                "postProcessBeanFactory already called on this post-processor against " + beanFactory);
    }
    this.factoriesPostProcessed.add(factoryId);
    if (!this.registriesPostProcessed.contains(factoryId)) {
        //这里处理
        processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
    }

    enhanceConfigurationClasses(beanFactory);
    beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
}

//processConfigBeanDefinitions
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
    String[] candidateNames = registry.getBeanDefinitionNames();

    for (String beanName : candidateNames) {
        BeanDefinition beanDef = registry.getBeanDefinition(beanName);
        if (beanDef.getAttribute(ConfigurationClassUtils.CONFIGURATION_CLASS_ATTRIBUTE) != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
            }
        }
        //判断每个bean定义是否是配置类
        //被@Configuration，@Component，@ComponentScan，@Import，@ImportResource标记
        //如果是则会给这个bean definition增加一个属性，避免重复解析
        else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
            configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
        }
    }
    //...
    //解析
    //parse -> processConfigurationClass -> doProcessConfigurationClass
    parser.parse(candidates);
    //...
}


protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass)
      throws IOException {
   if (configClass.getMetadata().isAnnotated(Component.class.getName())) { 
      //先递归处理内部类
      processMemberClasses(configClass, sourceClass);
   }

   //处理配置类上的@PropertySource
   for (AnnotationAttributes propertySource : AnnotationConfigUtils.attributesForRepeatable(
         sourceClass.getMetadata(), PropertySources.class,
         org.springframework.context.annotation.PropertySource.class)) {
      if (this.environment instanceof ConfigurableEnvironment) {
         processPropertySource(propertySource);
      }
      else {
         logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() +
               "]. Reason: Environment must implement ConfigurableEnvironment");
      }
   }

   //处理配置类上的@ComponentScan注解
   Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(
         sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
   if (!componentScans.isEmpty() &&
         // 检查是否跳过扫描
         !this.conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
      for (AnnotationAttributes componentScan : componentScans) {
         // The config class is annotated with @ComponentScan -> perform the scan immediately 扫描BeanDefinition
         Set<BeanDefinitionHolder> scannedBeanDefinitions =
               this.componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
         // Check the set of scanned definitions for any further config classes and parse recursively if needed
         for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
            BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
            if (bdCand == null) {
               bdCand = holder.getBeanDefinition();
            }
            // 判断指定的BeanDefinition是否是配置类，如果是，则递归解析扫描出来的该BeanDefinition
            if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, this.metadataReaderFactory)) {
               parse(bdCand.getBeanClassName(), holder.getBeanName());
            }
         }
      }
   }

   //处理配置类上的@Import注解
   processImports(configClass, sourceClass, getImports(sourceClass), true);

   //处理配置类上的@ImportResource注解
   AnnotationAttributes importResource =
         AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
   if (importResource != null) {
      String[] resources = importResource.getStringArray("locations");
      Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
      for (String resource : resources) {
         String resolvedResource = this.environment.resolveRequiredPlaceholders(resource);
         configClass.addImportedResource(resolvedResource, readerClass);
      }
   }

   //处理配置类中的@Bean注解方法
   Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
   for (MethodMetadata methodMetadata : beanMethods) {
      // 记录BeanMethod到configClass
      configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
   }

   //处理sourceClass配置类实现的接口上的默认方法
   processInterfaces(configClass, sourceClass);

   //处理父类
   if (sourceClass.getMetadata().hasSuperClass()) {
      String superclass = sourceClass.getMetadata().getSuperClassName();
      if (superclass != null && !superclass.startsWith("java") &&
            !this.knownSuperclasses.containsKey(superclass)) {
         this.knownSuperclasses.put(superclass, configClass);
         //递归处理
         return sourceClass.getSuperClass();
      }
   }
   return null;
}
```


<br/>
<hr/>

## springboot 自动装配

使用springboot简单示例：

**pom文件：**
``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>work.icql</groupId>
    <artifactId>test</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <!--springboot-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.4.3</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

**启动类：**
``` java
package work.icql.test;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 1）@SpringBootApplication

``` java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
}
```

@SpringBootApplication 看作是 @Configuration、@EnableAutoConfiguration、@ComponentScan 注解的集合

>**（1）@EnableAutoConfiguration：**
>启用 SpringBoot 的自动配置机制
>**（2）@Configuration：**
>允许在上下文中注册额外的 bean 或导入其他配置类
>**（3）@ComponentScan：**
>扫描被@Component (@Service,@Controller)注解的 bean
>注解默认会扫描启动类所在的包下所有的类
>可以自定义不扫描某些 bean

### 2）@EnableAutoConfiguration

``` java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
}
```

可以看到是通过 @Import 注入 AutoConfigurationImportSelector 这个bean

### 3）AutoConfigurationImportSelector

>AutoConfigurationImportSelector 类实现了 ImportSelector接口
>接口中 selectImports 方法用于返回需要加载到ioc容器中的类的全限定名
>AutoConfigurationImportSelector 具体实现是从所有jar META-INF/spring.factories 中获取类的全限定类名

``` java
@Override
public String[] selectImports(AnnotationMetadata annotationMetadata) {
    //判断自动装配开关是否打开
    if (!isEnabled(annotationMetadata)) {
        return NO_IMPORTS;
    }
    //获取所有加载的bean
    AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
    return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
}

protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
    if (!isEnabled(annotationMetadata)) {
        return EMPTY_ENTRY;
    }
    AnnotationAttributes attributes = getAttributes(annotationMetadata);
    //从 META-INF/spring.factories 中获取配置类
    //里边调用 SpringFactoriesLoader 类的方法
    List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    configurations = removeDuplicates(configurations);
    Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    checkExcludedClasses(configurations, exclusions);
    //排除不解析的配置类
    configurations.removeAll(exclusions);
    //注意这个 filter 方法
    configurations = getConfigurationClassFilter().filter(configurations);
    fireAutoConfigurationImportEvents(configurations, exclusions);
    return new AutoConfigurationEntry(configurations, exclusions);
}

//过滤不需要加载的 spring.factories 中的类
List<String> filter(List<String> configurations) {
    long startTime = System.nanoTime();
    String[] candidates = StringUtils.toStringArray(configurations);
    boolean skipped = false;
    //filter过滤器
    for (AutoConfigurationImportFilter filter : this.filters) {
        boolean[] match = filter.match(candidates, this.autoConfigurationMetadata);
        for (int i = 0; i < match.length; i++) {
            if (!match[i]) {
                candidates[i] = null;
                skipped = true;
            }
        }
    }
    if (!skipped) {
        return configurations;
    }
    List<String> result = new ArrayList<>(candidates.length);
    for (String candidate : candidates) {
        if (candidate != null) {
            result.add(candidate);
        }
    }
    if (logger.isTraceEnabled()) {
        int numberFiltered = configurations.size() - result.size();
        logger.trace("Filtered " + numberFiltered + " auto configuration class in "
                + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime) + " ms");
    }
    return result;
}

//filter过滤器，AutoConfigurationImportFilter，对应的过滤注解

@ConditionalOnBean：当容器里有指定 Bean 的条件下
@ConditionalOnMissingBean：当容器里没有指定 Bean 的情况下
@ConditionalOnSingleCandidate：当指定 Bean 在容器中只有一个，或者虽然有多个但是指定首选 Bean
@ConditionalOnClass：当类路径下有指定类的条件下
@ConditionalOnMissingClass：当类路径下没有指定类的条件下
@ConditionalOnProperty：指定的属性是否有指定的值
@ConditionalOnResource：类路径是否有指定的值
@ConditionalOnExpression：基于 SpEL 表达式作为判断条件
@ConditionalOnJava：基于 Java 版本作为判断条件
@ConditionalOnJndi：在 JNDI 存在的条件下差在指定的位置
@ConditionalOnNotWebApplication：当前项目不是 Web 项目的条件下
@ConditionalOnWebApplication：当前项目是 Web 项目的条件下
```


<br/>
<hr/>

## springboot 启动

![spring_springboot启动流程](../../../resource/spring_springboot启动流程.png)

<br/>
<hr/>