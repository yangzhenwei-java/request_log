package com.github.log.filter.common;

import com.github.log.filter.CommonFilter;
import com.github.log.filter.FilterChain;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 *  BeanPostProcessor是由spring提供的,在spring容器中每个类的初始化都会调用此类中提供的方法
 */
public class BeanPostProcessorFilter implements BeanPostProcessor {

	/**
	 * 类初始化前会调用此方法
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
     */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 *  类初始化完成后调用次方法
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
     */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof BeebanKFilterAware){
			FilterChain.addFilter((CommonFilter)bean);
			FilterChain.sort();
		}
		return bean;
	}
	
	

}
