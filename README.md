# request_log
javaweb 基于springmvc记录每次请求与响应的日志


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
	      
      
    

