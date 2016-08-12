package com.github.log.conf;

import com.github.log.filter.LogHandler;
import com.github.log.filter.common.UrlInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.StringUtils;
import com.github.log.filter.common.BeanPostProcessorFilter;
import com.github.log.filter.impl.LogFilter;
import com.github.log.filter.impl.MDCFilter;

import java.util.Map;


/**
 * 此类是日志功能的配置类
 */
public class FilterConfigurer implements BeanDefinitionRegistryPostProcessor {

    // 日志的扩展处理类
    private LogHandler logHandler;

//    // 记录日志的规则集合
//    private String[] includePatterns;
    //  排除记录日志的规则集合
    private String[] excludePatterns;

    // key为url地址   value为是否记录返回内容
    private Map<String,Boolean> includePatternsMap;

    // 返回内容的大小
    private Integer responseSize = 1024;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor(new BeanPostProcessorFilter());
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        BeanDefinitionBuilder mdcfilterBuilder = BeanDefinitionBuilder.genericBeanDefinition(MDCFilter.class);
        AbstractBeanDefinition mdcfilterDefinition = mdcfilterBuilder.getBeanDefinition();
        registry.registerBeanDefinition("mdcfilter", mdcfilterDefinition);

        BeanDefinitionBuilder logfilterBuilder = BeanDefinitionBuilder.genericBeanDefinition(LogFilter.class);
        logfilterBuilder.addPropertyValue("urlInterceptor", new UrlInterceptor(includePatternsMap, excludePatterns));

        if (StringUtils.isEmpty(logHandler)) {
            System.err.println("警告:没有实现com.beebank.common.filter.LogHandler类");
        } else {

            logfilterBuilder.addPropertyValue("logHandler", logHandler);
        }
        logfilterBuilder.addPropertyValue("filterConfigurer",this);
        AbstractBeanDefinition logfilterDefinition = logfilterBuilder.getBeanDefinition();
        registry.registerBeanDefinition("logfilter", logfilterDefinition);


    }


    public LogHandler getLogHandler() {
        return logHandler;
    }

    public void setLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }

//    public String[] getIncludePatterns() {
//        return includePatterns;
//    }
//
//    public void setIncludePatterns(String[] includePatterns) {
//        this.includePatterns = includePatterns;
//    }


    public Map<String, Boolean> getIncludePatternsMap() {
        return includePatternsMap;
    }

    public void setIncludePatternsMap(Map<String, Boolean> includePatternsMap) {
        this.includePatternsMap = includePatternsMap;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    public void setExcludePatterns(String[] excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    public Integer getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(Integer responseSize) {
        this.responseSize = responseSize;
    }
}
