package com.github.log.filter;

import java.util.List;


import com.github.log.filter.wrapper.WrapperedResponse;

public interface UriStrategies {
	
	/**
	 * 获取所有请求要拦截的url
	 * @return
	 */
//	public List<String> requestUrls();
	
	
	/**
	 * 不拦截的URL 集合
	 *  url 可以为正则表达式如/img*
	 * @return
	 */
	public List<String> excludeUrls();
	
	
//	/**
//	 * 
//	 * 将要拦截的请求去正则匹配
//	 * @param pattern
//	 * @return
//	 */
//	public Pattern requestUrlRegexp();
//	
//	/**
//	 * 将要拦截的响应用正则匹配
//	 * @param pattern
//	 * @return
//	 */
//	public Pattern responseUrlRegexp();
	
	
	/**
	 * 计算记录日志内容
	 * @return
	 */
	public byte[] calculateLogResContents(WrapperedResponse res);
	
	

}
