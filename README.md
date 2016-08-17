# request_log
javaweb 基于springmvc记录每次请求与响应的日志

***一 实现的功能***

	
	1）咱们是否想记录某些请求传入进来的参数与响应回去的内容呢？
	但是按照传统的方案只能是针对每个请求读取流然后记录日志在进行业务逻辑处理。
	无法运用aop的思想去统一处理这类问题。request_log解决了这类问题。通过配置文件
	可以灵活的配置自己想要拦截的请求**
	
	
	2）传统的日志格式如： <pattern>[%thread] %-5level %logger{35} - %msg %n</pattern>
	是否想要额外的记录一下自己定义的信息呢？如当前用户的名字、请求的url、请求的方式、响应码等。
	request_log可以使你随心所欲的改造日志的格式并进行个性化的扩展。增加自己想要记录的内容。
	如：<pattern>[%t]%d %p %logger{35} |%X{exe_time}|%X{session_id}|%X{user_no}|%X{user_name}|%X{control_name}|%X{uri}|%X{methed}|%X{req_str}|%X{query_string}|%X{res_str}|%m%n</pattern>
	
	备注：
			MDC.put("client_ip", getIpAddr(req)+"");
			MDC.put("req_str", new String(req.getByteArray(),"UTF-8"));
			MDC.put("methed", req.getMethod());
			MDC.put("start_time", System.currentTimeMillis()+"");
			MDC.put("uri", req.getServletPath());
			MDC.put("query_string", req.getQueryString()+"");
			MDC.put("service_ip", service_ip);
			MDC.put("thread_key", UUID.randomUUID().toString().substring(0,5));
			MDC.put("system_key","assetmanage-monitor");
			MDC.put("control_name", req.getRequestURI());

***二 使用方法***

**1. git clone https://github.com/yangzhenwei-java/request_log.git**

**2. cd request_log && mvn clean install** 

**3. 引入项目中,在pom.xml添加**

		<dependency>
			<groupId>com.github</groupId>
			<artifactId>request_log</artifactId>
			<version>${version}</version>
		</dependency>
		
**4.  在spring的配置文件中加入:**

    <bean class="com.github.log.conf.FilterConfigurer" >
      <property name="logHandler" ref="redisLogQueue" />
      <property name="includePatternsMap">
        <map>
          <entry key="/service/login/**" value="false"></entry>
          <entry key="/Weixin/**" value="true"></entry>
        </map>
      </property>
    </bean>
    
    1) logHandler非必需,此处是对日志后续处理的扩展点。可实现com.github.log.filter.LogHandler此接口对日志进行处理。
        如过滤或存储数据库等。需要实现此接口中的execute方法。
        
    2）includePatternsMap非必需,此处主要是根据相应的url选择性的记录日志。key是url,支持路径匹配。下面会详细讲解。
    value="false"表示不记录响应内容。因为有些响应内容比较多且没有价值，如响应内容是Html页面。
	    备注：AntUrlPathMatcher为我们提供了三种通配符。
	      通配符：?
	      示例：/admin/g?t.jsp
	      匹配任意一个字符，/admin/g?t.jsp可以匹配/admin/get.jsp和/admin/got.jsp或是/admin/gxt.do。不能匹配/admin/xxx.jsp。
	      通配符：*
	      示例：/admin/*.jsp
	      匹配任意多个字符，但不能跨越目录。/*/index.jsp可以匹配/admin/index.jsp和/user/index.jsp，但是不能匹配/index.jsp和/user/test/index.jsp。
	      通配符：**
	      示例：/**/index.jsp
	      可以匹配任意多个字符，可以跨越目录，可以匹配/index.jsp，/admin/index.jsp，/user/admin/index.jsp和/a/b/c/d/index.jsp
	      
**5. 在web.xml文件中加入:**

	<filter>
		<filter-name>FilterChain</filter-name>
		<filter-class>com.github.log.filter.FilterChain</filter-class>
		<init-param>
			<param-name>ignoreUrlSuffixs</param-name>
			<param-value>\.(.*)</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>FilterChain</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
     	
     	1)  ignoreUrlSuffixs：根据后缀名忽略拦截。如jquery.js、xxx.jpg。\.(.*)表示忽略所有url中包含【.】字符的请求。
     	

