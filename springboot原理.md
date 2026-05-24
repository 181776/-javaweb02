1. ### 起步依赖

假如我们没有使用SpringBoot，用的是Spring框架进行web程序的开发，此时我们就需要引入web程序开发所需要的一些依赖。

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MWUxYzQ0NjE1OWE5ZDdmM2ZlZjg4YjQ3MzRhZmU5OGVfZzNSUnJnRHdNSFY1ckVOUG1YNEd5T0EzbWhWODd3aG1fVG9rZW46TzB5Z2JGR3hEbzkyb054c21QMWM0djJlbmFiXzE3NzkzNjUwODY6MTc3OTM2ODY4Nl9WNA)

当我们引入了 spring-boot-starter-web 之后，maven会通过**依赖传递**特性，将web开发所需的常见依赖都传递下来。

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MGM0MWMwNzQwY2YwMzZkY2UyNTFhYjhhOTFjZjc5ZWZfejgyUUdEVUlVd2J3SVFUejZpbXZFdzM4Yndsc29EWnNfVG9rZW46UElHWmJWSVJSb2tmNXJ4MjJ1TGNkMmVYbjllXzE3NzkzNjUwODY6MTc3OTM2ODY4Nl9WNA)

所以，起步依赖的原理就是Maven的依赖传递。

- 在SpringBoot给我们提供的这些起步依赖当中，已提供了当前程序开发所需要的所有的常见依赖(官网地址：https://docs.spring.io/spring-boot/docs/2.7.7/reference/htmlsingle/#using.build-systems.starters)。

- 比如：springboot-starter-web，这是web开发的起步依赖，在web开发的起步依赖当中，就集成了web开发中常见的依赖：json、web、webmvc、tomcat等。我们只需要引入这一个起步依赖，其他的依赖都会自动的通过Maven的依赖传递进来。

- ### 自动配置

SpringBoot的自动配置就是当spring容器启动后，一些配置类、bean对象就自动存入到了IOC容器中，不需要我们手动去声明，从而简化了开发，省去了繁琐的配置操作。

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZjdlMmNjZTZhZGIyNzIzYzE2ZjljMDdmNDVlNzY5ZjZfa1ZXWDY0ZWdQVzYySXVxMDZXOURLaXowM0xmYXJDQ3VfVG9rZW46QzFic2JkcEU0bzBTaW54SU05emNGRTlGbmtjXzE3NzkzNjUxMTI6MTc3OTM2ODcxMl9WNA)

比如，在我们前面讲解AOP记录日志的那个案例中，我们要将一个对象转为json，直接注入一个Gson，然后就可以直接使用了。而我们在我们整个项目中，也并未配置Gson这个类型的bean，为什么可以直接注入使用呢？ 原因就是因为这个bean，springboot中已经帮我们自动配置完毕了，我们是可以直接使用的。

那接下来，我们就要来解析，SpringBoot中到底是如何完成自动配置的。

1. #### 实现方案

我们知道了什么是自动配置之后，接下来我们就要来剖析自动配置的原理。解析自动配置的原理就是分析在 SpringBoot项目当中，我们引入对应的依赖之后，是如何将依赖jar包当中所提供的bean以及配置类直接加载到当前项目的SpringIOC容器当中的。

接下来，我们就直接通过代码来分析自动配置原理。

- 准备工作：在Idea中导入"资料\03. 自动配置原理" 下的 `itheima-utils` 工程
- 在SpringBoot项目 `spring-boot-web-config` 工程中，通过坐标引入`itheima-utils`依赖

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=Nzk0MWY5Y2Q2MWY3OTJlOTAzY2Y5YjFkYzQyNzc4OThfN3k5TlJNaDNqb1dBTVlJSFQ4cldMSlFtbUVkSk5jSEhfVG9rZW46S2Q2cGJqTEpob3RYUTV4cEhYVWNjNnJjblplXzE3NzkzNjUxMTI6MTc3OTM2ODcxMl9WNA)

1、引入的 `itheima-utils` 中配置如下:

```Java
@Component
public class TokenParser {
    public void parse(){
        System.out.println("TokenParser ... parse ...");
    }
}
```

2、在测试类中，添加测试方法

```Java
@SpringBootTest
public class AutoConfigurationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testTokenParse(){
        System.out.println(applicationContext.getBean(TokenParser.class));
    }

    //省略其他代码...
}
```

3、执行测试方法

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ODU5NDI4ZTRiOGNkNjFhMDcyOTc1NjMwZDk1OTJiMDdfUk9WTjE1RkdLWjRpR0l1N3RLaTBBbVo3bldxbmJxSWVfVG9rZW46RU5FWGJZRjlab24xdER4ZlZScWNpQ2s1bmhlXzE3NzkzNjUxMTI6MTc3OTM2ODcxMl9WNA)

> 异常信息描述： 没有com.example.TokenParse类型的bean
>
> 说明：在Spring容器中没有找到com.example.TokenParse类型的bean对象

**思考：引入进来的第三方依赖当中的bean以及配置类为什么没有生效？**

- 原因在我们之前讲解IOC的时候有提到过，在类上添加`@Component`注解来声明bean对象时，还需要保证`@Component`注解能被Spring的组件扫描到。
- SpringBoot项目中的`@SpringBootApplication`注解，具有包扫描的作用，但是它只会扫描启动类所在的当前包以及子包。 
- 当前包：com.itheima， 第三方依赖中提供的包：com.example（扫描不到）

**那么如何解决以上问题的呢？**

- 方案1：`@ComponentScan` 组件扫描

- 方案2：`@Import` 导入（使用`@Import`导入的类会被Spring加载到IOC容器中）

- ##### 方案一

`@ComponentScan`组件扫描

```Java
@SpringBootApplication
@ComponentScan({"com.itheima","com.example"}) //指定要扫描的包
public class SpringbootWebConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebConfigApplication.class, args);
    }
}
```

重新执行测试方法，控制台日志输出：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=OWFjMDYxZmJlOTJmYzY0NTVjMzNkZWEwNDUzMDNlZjhfNVlpa0JtZFVyMmRpV1Fxc2hkSWUyNGZ0ZDUza25tVFlfVG9rZW46UEt1aWJkQXRVb3dGbGV4VmlUOGNNcEpWbnJkXzE3NzkzNjUxNDM6MTc3OTM2ODc0M19WNA)

大家可以想象一下，如果采用以上这种方式来完成自动配置，那我们进行项目开发时，当需要引入大量的第三方的依赖，就需要在启动类上配置N多要扫描的包，这种方式会很繁琐。而且这种大面积的扫描性能也比较低。

**缺点：**

1. 使用繁琐
2. 性能低

**结论：SpringBoot中并没有采用以上这种方案。**

1. ##### 方案二

@Import导入

- 导入形式主要有以下几种：
  - 导入普通类
  - 导入配置类
  - 导入ImportSelector接口实现类

**1). 使用@Import****导入普通类****：**

```Java
@Import(TokenParser.class) //导入的类会被Spring加载到IOC容器中
@SpringBootApplication
public class SpringbootWebConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebConfigApplication.class, args);
    }
}
```

重新执行测试方法，控制台日志输出：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZjE5OTkwMGUwNTdmZjY1NzRmYzgyYjgzY2JjMjYxMGJfUllkVVZlVHlBZlZMRDIyRGNGbDRhcmRlTUFxRnVSeHhfVG9rZW46RkpZaGI0NkIxb3JrWFl4YlJTOGNwaFhEbmhkXzE3NzkzNjUxNDM6MTc3OTM2ODc0M19WNA)

**2). 使用@Import导入配置类：**

- 配置类

```Java
@Configuration
public class HeaderConfig {
    @Bean
    public HeaderParser headerParser(){
        return new HeaderParser();
    }

    @Bean
    public HeaderGenerator headerGenerator(){
        return new HeaderGenerator();
    }
}
```

- 启动类

```Java
@Import(HeaderConfig.class) //导入配置类
@SpringBootApplication
public class SpringbootWebConfig2Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebConfig2Application.class, args);
    }
}
```

- 测试类

```Java
@SpringBootTest
public class AutoConfigurationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testHeaderParser(){
        System.out.println(applicationContext.getBean(HeaderParser.class));
    }

    @Test
    public void testHeaderGenerator(){
        System.out.println(applicationContext.getBean(HeaderGenerator.class));
    }
    
    //省略其他代码...
}
```

执行测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZDUxZmU3MDgyNGFjZmU3N2IwMDM3ZGIxMDg4OGY0OTRfdkZ5NFZPOXJmbjZ0V25BNHQ2VXZHUEcycmNha0JFaG5fVG9rZW46SEdhRWJhOXRpb2lxZGF4MzhlY2NNNnB6bmxoXzE3NzkzNjUxNDM6MTc3OTM2ODc0M19WNA)

**3). 使用@Import导入ImportSelector接口实现类：**

- ImportSelector接口实现类

```Java
public class MyImportSelector implements ImportSelector {
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //返回值字符串数组（数组中封装了全限定名称的类）
        return new String[]{"com.example.HeaderConfig"};
    }
}
```

- 启动类

```Java
@Import(MyImportSelector.class) //导入ImportSelector接口实现类
@SpringBootApplication
public class SpringbootWebConfig2Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebConfig2Application.class, args);
    }
}
```

执行测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=N2QyZDBkNGUxM2YwZmJjZTAxZGQxZGJhZDViMWU0NDlfRE9KcWd2T2VJZDBhOTZ4bWZwb3dzUXBmOWpJZThZaHBfVG9rZW46SjZkU2JVTTM5b2h6N014NVRKNmNjN3JXblpiXzE3NzkzNjUxNDM6MTc3OTM2ODc0M19WNA)

我们使用@Import注解通过这三种方式都可以导入第三方依赖中所提供的bean或者是配置类。

思考：如果基于以上方式完成自动配置，当要引入一个第三方依赖时，是不是还要知道第三方依赖中有哪些配置类和哪些Bean对象？

- 答案：是的。 （对程序员来讲，很不友好，而且比较繁琐）

思考：当我们要使用第三方依赖，依赖中到底有哪些bean和配置类，谁最清楚？

- 答案：第三方依赖自身最清楚。

**结论：我们不用自己指定要导入哪些bean对象和配置类了，让第三方依赖它自己来指定。**

怎么让第三方依赖自己指定bean对象和配置类？

- 比较常见的方案就是第三方依赖给我们提供一个注解，这个注解一般都以@EnableXxxx开头的注解，注解中封装的就是@Import注解

**4****). 使用第三方依赖提供的 @EnableXxxxx注解**

- 第三方依赖中提供的注解

```Java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(MyImportSelector.class)//指定要导入哪些bean对象或配置类
public @interface EnableHeaderConfig { 
}
```

- 在使用时只需在启动类上加上@EnableXxxxx注解即可

```Java
@EnableHeaderConfig  //使用第三方依赖提供的Enable开头的注解
@SpringBootApplication
public class SpringbootWebConfig2Application {
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebConfig2Application.class, args);
    }
}
```

执行测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=YTNiMDBhYmVmMGY1NDEzZjFhOGFhZWU4MjkyNWRkMzJfQmJyUUEwdk9pR2NHMGhJbXBjaHowdjM5QUxWSGwyYWZfVG9rZW46TVpNbGJjcFl4b3pYNkR4OGRJUmNYcWQzbjBkXzE3NzkzNjUxNDM6MTc3OTM2ODc0M19WNA)

以上四种方式都可以完成导入操作，但是第4种方式会更方便更优雅，而这种方式也是SpringBoot当中所采用的方式。

1. #### 原理分析

1. ##### 源码跟踪

前面我们讲解了在项目当中引入第三方依赖之后，如何加载第三方依赖中定义好的bean对象以及配置类，从而完成自动配置操作。那下面我们通过源码跟踪的形式来剖析下SpringBoot底层到底是如何完成自动配置的。

**源码跟踪技巧：**

在跟踪框架源码的时候，一定要抓住关键点，找到核心流程。一定不要从头到尾一行代码去看，一个方法的去研究，一定要找到关键流程，抓住关键点，先在宏观上对整个流程或者整个原理有一个认识，有精力再去研究其中的细节。

要搞清楚SpringBoot的自动配置原理，要从SpringBoot启动类上使用的核心注解`@SpringBootApplication`开始分析：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=M2IxMDYzZjM2MzExZDU0ZWEzMzBkODkzNzMxNzQ5NGFfR093VVliQkFsbFRlSDA3cExPV0tXNW5KTTRSTjlmeWVfVG9rZW46UFJkTWJTMnpJb1o0VFh4eU9OT2NiOG5ablRjXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

在`@SpringBootApplication`注解中包含了：

- 元注解（不再解释）
- `@SpringBootConfiguration`
- `@EnableAutoConfiguration`
- `@ComponentScan`

- **我们先来看第一个注解：****`@SpringBootConfiguration`**

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MDJkZGMzZjRkYjgxN2M0YWJiMTNjMDljMzQ5ZWVkYTNfVTRIMDVWZXdoSXdYdHYwSEVDcUFsWm41QjA2UWlUVklfVG9rZW46SmFBbGJ1T3dBbzVuWHJ4bkZ3NmNFWk1Rbk5kXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> @SpringBootConfiguration注解上使用了@Configuration，表明SpringBoot启动类就是一个配置类。
>
> @Indexed注解，是用来加速应用启动的（不用关心）。

- **接下来再先看****`@ComponentScan`****注解：**

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=NTliZWZjNjc4ZmVjODk5MWJkODJmNTc3ZDU1ZThlODFfd1U2VTFITjVka0xHWEhtQWlYVmJWQVNKZmE1WG1MR2FfVG9rZW46Qzh1OGJab1RTb1VJc054ajZpNGNZUUFLbndoXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> @ComponentScan注解是用来进行组件扫描的，扫描启动类所在的包及其子包下所有被@Component及其衍生注解声明的类。
>
> SpringBoot启动类，之所以具备扫描包功能，就是因为包含了@ComponentScan注解。

- **最后我们来看看****`@`****`Enable`****`AutoConfiguration`****注解（自动配置核心注解）：**

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZWY0ZDZhMWJmZTQ4M2U1MmY5NTlkZWUyNmZiZWEzM2JfUDdGeXBHeWZvYUpIcHpCdzF5bnBJbkhDbXBlZTRyelBfVG9rZW46UWhXdmIxZm5zb2phTTB4QlFRZmM4Qk9ObmRoXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

使用`@Import`注解，导入了实现`ImportSelector`接口的实现类。

`AutoConfigurationImportSelector`类是`ImportSelector`接口的实现类。

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MGNjNzk1ZGQ5ZmMwNTQyMzEzMjYxYWJmNDgxYzUyMTJfbUl6dDNXWHJZZW00T0dBNTA0QmswUGc5Q1RDWUhJNmlfVG9rZW46RG5TM2JlMjgwb3ZZQXB4ZENoVmNJbWM5bmRlXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

`AutoConfigurationImportSelector`类中重写了`ImportSelector`接口的`selectImports()`方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZDVmMTA1OGU0OTU1ZDA2MjA5ZDc2ZTZjN2EwN2JhMWVfdWExS0djb1M0akVUQlBzRW1OQWVHeWZQTmZIaFE4aVRfVG9rZW46VjI0YWIwbUpCb0o5N3V4MWU2OWNtUTR5bkllXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> selectImports()方法底层调用getAutoConfigurationEntry()方法，获取可自动配置的配置类信息集合

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZWNjNjJmOWU5MzAxOWRiOTEyOWY2MjMzZDZhZmY4MjBfWHdjMzVINlJScE15S3BhZkpNd0Ztc3BqN3JhNDJNQnRfVG9rZW46Wm9KemJ1NGRmb0R0eFd4Mml1bmNqUlFSbnFoXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> getAutoConfigurationEntry()方法通过调用getCandidateConfigurations(annotationMetadata, attributes)方法获取在配置文件中配置的所有自动配置类的集合

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=NGY1YzQyMzFkNmQ2NjdiZjY2ZGJlMzQ4ZmRlYzIwYjZfNDZCU204WnJkVTVQUkFsWlZ6NWRNdFJPUUN0TDhwWnFfVG9rZW46UVgxQWI2Nkc1b1ZvOXB4YjdxNmNpRDZ6blJZXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> `getCandidateConfigurations`方法的功能：
>
> 获取所有基于 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件中配置类的集合

`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件这两个文件在哪里呢？

- 通常在引入的起步依赖中，都有包含以上文件 

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=NjcwYzcxMmIyZDE1ZTAxYWZhODEzN2M0NzlmYjNhNjRfYkVMazJUOVdGRU4zRVNGRmh0ZnBzaUVoVVBCRkVSSnBfVG9rZW46TXBNQWJCWlVhb3Fxekd4RFBxWmNtcGh3bkNnXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

在前面在给大家演示自动配置的时候，我们直接在测试类当中注入了一个叫`gson`的bean对象，进行JSON格式转换。虽然我们没有配置bean对象，但是我们是可以直接注入使用的。原因就是因为在自动配置类当中做了自动配置。到底是在哪个自动配置类当中做的自动配置呢？我们通过搜索来查询一下。

在`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 配置文件中指定了第三方依赖Gson的配置类：`GsonAutoConfiguration`

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=YTcwMjcyODZmYjMyMTM3MWM5YjA1OTE1MDZhNjJmMWJfYld2OXRXcFZMWVR1MUxJRE95OURBNEpZRlpJSGJLc0NfVG9rZW46VnFRQmJZa0tVb2R5SmV4UWNlU2NBOFk0blRvXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

打开上面的第三方依赖中提供的 `GsonAutoConfiguration` 类：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZTAwMTFhMmMxYTQ4YmY4NTgyZWViYzZiZTU5MmY5ZmNfV0xoeTZZdVMzM0hYOWdnU2F1MW91UXBWVTFyT3d3eXFfVG9rZW46UEE3NGI5RlFab1RoT0R4YmZXcWNTbkhkbm9mXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

在`GsonAutoConfiguration`类上，添加了注解`@AutoConfiguration`，通过查看源码，可以明确：`GsonAutoConfiguration` 类是一个配置。

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MWFhZGNhZjIxYzg1NzQ5ZGUwMGVlNzMyYzM4ZmE5MzdfY3dodW42aThVRmtRUU5LNkp4azIwemw4SWpneDZFa29fVG9rZW46S0NQMGJsTzJjb25VN0R4U0xhN2NwWTZXbndlXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

看到这里，大家就应该明白为什么可以完成自动配置了，原理就是在配置类中定义一个`@Bean`标识的方法，而Spring会自动调用配置类中使用`@Bean`标识的方法，并把方法的返回值注册到IOC容器中。

**自动配置源码小结**

自动配置原理源码入口就是 `@SpringBootApplication` 注解，在这个注解中封装了3个注解，分别是：

- @SpringBootConfiguration
  - 声明当前类是一个配置类
- @ComponentScan
  - 进行组件扫描（SpringBoot中默认扫描的是启动类所在的当前包及其子包）
- @EnableAutoConfiguration
  - 封装了@Import注解（Import注解中指定了一个ImportSelector接口的实现类）
  -  在实现类重写的selectImports()方法，读取当前项目下所有依赖jar包中`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`两个文件里面定义的配置类（配置类中定义了@Bean注解标识的方法）。

当SpringBoot程序启动时，就会加载配置文件当中所定义的配置类，并将这些配置类信息(类的全限定名)封装到String类型的数组中，最终通过@Import注解将这些配置类全部加载到Spring的IOC容器中，交给IOC容器管理。

> 最后呢给大家抛出一个问题：在 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件中定义的配置类非常多，而且每个配置类中又可以定义很多的bean，那这些bean都会注册到Spring的IOC容器中吗？
>
> 答案：并不是。 在声明bean对象时，上面有加一个以 `@Conditional` 开头的注解，这种注解的作用就是按照条件进行装配，只有满足条件之后，才会将bean注册到Spring的IOC容器中（下面会详细来讲解）

1. ##### @Conditional

我们在跟踪SpringBoot自动配置的源码的时候，在自动配置类声明bean的时候，除了在方法上加了一个@Bean注解以外，还会经常用到一个注解，就是以Conditional开头的这一类的注解。以Conditional开头的这些注解都是条件装配的注解。下面我们就来介绍下条件装配注解。

**@Conditional注解：**

- 作用：按照一定的条件进行判断，在满足给定条件后才会注册对应的bean对象到Spring的IOC容器中。
- 位置：方法、类
- @Conditional本身是一个父注解，派生出大量的子注解：
  - @ConditionalOnClass：判断环境中有对应字节码文件，才注册bean到IOC容器。
  - @ConditionalOnMissingBean：判断环境中没有对应的bean(类型或名称)，才注册bean到IOC容器。
  - @ConditionalOnProperty：判断配置文件中有对应属性和值，才注册bean到IOC容器。

下面我们通过代码来演示下Conditional注解的使用：

- **`@ConditionalOnClass`****注解**

```Java
@Configuration
public class HeaderConfig {

    @Bean
    @ConditionalOnClass(name="io.jsonwebtoken.Jwts")//环境中存在指定的这个类，才会将该bean加入IOC容器
    public HeaderParser headerParser(){
        return new HeaderParser();
    }
    
    //省略其他代码...
}
```

- pom.xml

```XML
<!--JWT令牌-->
<dependency>
     <groupId>io.jsonwebtoken</groupId>
     <artifactId>jjwt</artifactId>
     <version>0.9.1</version>
</dependency>
```

- 测试类

```Java
@SpringBootTest
public class AutoConfigurationTests {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testHeaderParser(){
        System.out.println(applicationContext.getBean(HeaderParser.class));
    }
    
    //省略其他代码...
}
```

执行testHeaderParser()测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MTI1ZDhiMGU3NTljMTZkNmFiNjIwMmIxODU3YTJhNDVfRDhYdmpHQWR3dEFTRmc1ekw4UVFCMkM0NWJlenp2TlBfVG9rZW46QVJEOGJWeUFFbzM0MVF4VFEzVGNrT3BPbm9kXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

因为 `io.jsonwebtoken.Jwts` 字节码文件在启动SpringBoot程序时已存在，所以创建HeaderParser对象并注册到IOC容器中。

- **@ConditionalOnMissingBean注解**

```Java
@Configuration
public class HeaderConfig {
        
    @Bean
    @ConditionalOnMissingBean //不存在该类型的bean，才会将该bean加入IOC容器
    public HeaderParser headerParser(){
        return new HeaderParser();
    }
    
    //省略其他代码...
}
```

执行testHeaderParser()测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=Y2VlZWM3Mzc0NzEwYzU3NmMyZjRkNTk1ZDg4NTNkMzVfOEVjSndGMkU0dVJrUFlDOXZSNXlHSWpaRkJOT3lHZExfVG9rZW46VmtoNmJmTEZNb29RVUl4OWIzTGNMTXdZbmRoXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

SpringBoot在调用@Bean标识的headerParser()前，IOC容器中是没有HeaderParser类型的bean，所以HeaderParser对象正常创建，并注册到IOC容器中。

- **再次修改****@ConditionalOnMissingBean注解**

```Java
@Configuration
public class HeaderConfig {

    @Bean
    @ConditionalOnMissingBean//不存在指定类型的bean，才会将该bean加入IOC容器
    public HeaderParser headerParser(){
        return new HeaderParser();
    }
    
    //省略其他代码...
}
```

执行testHeaderParser()测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MzBkMTlkZWQ2MGNlZGYxYzg4M2I4YThlZGQ5OTAyZTRfQklXdXBRclNUcWFtY2xWMzJMNU85MUpOZW5JSjRkVEZfVG9rZW46VUR0UmJoaXpub2FHSW14SlRkeGNrUHZ3bjJlXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

- **`@ConditionalOnProperty`****注解（这个注解和配置文件当中配置的属性有关系）**

先在`application.yml`配置文件中添加如下的键值对：

```YAML
name: itheima
```

在声明bean的时候就可以指定一个条件@ConditionalOnProperty

```Java
@Configuration
public class HeaderConfig {

    @Bean
    @ConditionalOnProperty(name ="name",havingValue = "itheima")//配置文件中存在指定属性名与值，才会将bean加入IOC容器
    public HeaderParser headerParser(){
        return new HeaderParser();
    }

    @Bean
    public HeaderGenerator headerGenerator(){
        return new HeaderGenerator();
    }
}
```

执行testHeaderParser()测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=ZmM0OTFkZjFjY2JjYzAzMjcwMzNlOWJkNjYzNzcyZTdfNzVJZ0o4eW9qQTVmbjV5ZGlBbFBsNFVSVldPNGc1Z1VfVG9rZW46QjVEN2J1TzZUb29ERnF4RXhNWWNDMUcwblplXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

修改`@ConditionalOnProperty`注解：  havingValue的值修改为"itheima2"

```Java
@Bean
@ConditionalOnProperty(name ="name",havingValue = "itheima2")//配置文件中存在指定属性名与值，才会将bean加入IOC容器
public HeaderParser headerParser(){
        return new HeaderParser();
}
```

再次执行testHeaderParser()测试方法：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MjUzOWQwNGE5ZGMzNTFmYThiNGVkNTcxODBkZTlkZThfSkc5OGhndnFDZHV0Q1pwVUREQzRaV1o0dGhScWFFb21fVG9rZW46WWVuOGJmTmRLb2NyTmx4OFRyWmM1MHNLblBkXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

> 因为 `application.yml` 配置文件中，不存在： name:  itheima2，所以HeaderParser对象在IOC容器中不存在

我们再回头看看之前讲解SpringBoot源码时提到的一个配置类：`GsonAutoConfiguration`

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=MTA2MjUzNTdiZjhjYTg4YzEwY2NjMDJmOWY0OGIxMjBfSFdyQUVXbEJJS28zWEx4dkxKOXlYVGxYVU90OFp4WmZfVG9rZW46RVE1Z2JFUkF3b1JkRHp4MTNiM2NMQkNkbk9jXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

最后再给大家梳理一下自动配置原理：

![img](https://heuqqdmbyk.feishu.cn/space/api/box/stream/download/asynccode/?code=NDMxMjA0YTdmOWUwNTQ2YTk2MmFhZDFjYzZiNzIyNjRfSjRyT1VpdnBWWGllUk5Yd3BZWDdEcTNtaUg1UllHaHVfVG9rZW46SmI1c2JzbGZWb1g3WFB4Q1ZkVGNGRmFnbmxmXzE3NzkzNjUxOTI6MTc3OTM2ODc5Ml9WNA)

自动配置的核心就在@SpringBootApplication注解上，SpringBootApplication这个注解底层包含了3个注解，分别是：

- @SpringBootConfiguration
- @ComponentScan
- @EnableAutoConfiguration

@EnableAutoConfiguration这个注解才是自动配置的核心。

- 它封装了一个@Import注解，Import注解里面指定了一个ImportSelector接口的实现类。
- 在这个实现类中，重写了ImportSelector接口中的selectImports()方法。
- 而selectImports()方法中会去读取两份配置文件，并将配置文件中定义的配置类做为selectImports()方法的返回值返回，返回值代表的就是需要将哪些类交给Spring的IOC容器进行管理。
- 那么所有自动配置类的中声明的bean都会加载到Spring的IOC容器中吗? 其实并不会，因为这些配置类中在声明bean时，通常都会添加@Conditional开头的注解，这个注解就是进行条件装配。而Spring会根据Conditional注解有选择性的进行bean的创建。
- @Enable 开头的注解底层，它就封装了一个注解 import 注解，它里面指定了一个类，是 ImportSelector 接口的实现类。在实现类当中，我们需要去实现 ImportSelector  接口当中的一个方法 selectImports 这个方法。这个方法的返回值代表的就是我需要将哪些类交给 spring 的 IOC容器进行管理。
- 此时它会去读取两份配置文件，一份儿是 spring.factories，另外一份儿是 autoConfiguration.imports。而在  autoConfiguration.imports 这份儿文件当中，它就会去配置大量的自动配置的类。
- 而前面我们也提到过这些所有的自动配置类当中，所有的 bean都会加载到 spring 的 IOC 容器当中吗？其实并不会，因为这些配置类当中，在声明 bean 的时候，通常会加上这么一类@Conditional 开头的注解。这个注解就是进行条件装配。所以SpringBoot非常的智能，它会根据 @Conditional 注解来进行条件装配。只有条件成立，它才会声明这个bean，才会将这个 bean 交给 IOC 容器管理。