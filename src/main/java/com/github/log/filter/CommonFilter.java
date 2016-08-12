package com.github.log.filter;

import com.github.log.exception.FilterException;
import com.github.log.filter.wrapper.WrapperedRequest;
import com.github.log.filter.wrapper.WrapperedResponse;

public interface CommonFilter {
	
//	/**
//	 * 是否执行RequestFilter方法,在请求前进行拦截
//	 * @param uri
//	 * @return
//	 */
//	public boolean isRequestFilter(String uri) ;
	
	/**
	 * 执行RequestFilter方法,在请求前进行拦截
	 * @param req
	 */
	public void  requestFilter(WrapperedRequest req, WrapperedResponse res) throws FilterException;
	
	
//	/**
//	 * 是否执行ResponseFilter方法,在请求后进行拦截
//	 * @param uri
//	 * @return
//	 */
//	public boolean isResponseFilter(String uri);
	
	/**
	 * 执行responseFilter方法,在请求后进行拦截
	 * @param res
	 */
	public void responseFilter(WrapperedRequest req,WrapperedResponse res) throws FilterException;
	
	
	
	/**
	 * 排序
	 * @return
	 */
	public int getOrder();
}
