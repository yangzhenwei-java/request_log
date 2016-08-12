package com.github.log.filter.impl;


import java.util.Arrays;
import java.util.HashMap;

import com.github.log.conf.FilterConfigurer;
import com.github.log.conf.MatchesRes;
import com.github.log.filter.LogHandler;
import com.github.log.filter.common.BeebanKFilterAware;
import com.github.log.filter.common.UrlInterceptor;
import com.github.log.filter.wrapper.WrapperedRequest;
import com.github.log.filter.wrapper.WrapperedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import com.github.log.filter.CommonFilter;


public class LogFilter implements CommonFilter, BeebanKFilterAware {

	// 日志处理的扩展类,客户端可以实现此类。来扩展日志的出处
	private LogHandler logHandler;

	// 此类用于判断是否需要记录日志
	private UrlInterceptor urlInterceptor;

	private FilterConfigurer filterConfigurer;

	private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);


	@Override
	public void responseFilter(WrapperedRequest req, WrapperedResponse res) {
		MatchesRes matchesRes  = urlInterceptor.matches(req.getServletPath());
		if (!matchesRes.isLog()) {
			return;
		}
		try {

			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("req_str", MDC.get("req_str"));
			hashMap.put("methed", MDC.get("methed"));
			hashMap.put("client_ip", MDC.get("client_ip"));
			hashMap.put("start_time", MDC.get("start_time"));
			hashMap.put("uri", MDC.get("uri"));
			hashMap.put("query_string", MDC.get("query_string"));
			hashMap.put("thread_key", MDC.get("thread_key"));
			hashMap.put("system_key", MDC.get("system_key"));
			hashMap.put("session_id", MDC.get("session_id"));
			hashMap.put("user_no", MDC.get("user_no"));
			hashMap.put("user_name", MDC.get("user_name"));
			Long end = System.currentTimeMillis();
			String resStr = "";
			if(matchesRes.isLogRes()){
				byte[] byteArray = res.getByteArray();
				if(byteArray.length<filterConfigurer.getResponseSize()){
					resStr = new String(byteArray, "UTF-8");
				}else {
					resStr = new String(Arrays.copyOf(byteArray,filterConfigurer.getResponseSize()), "UTF-8");
				}
			}


			long exe_time = end - Long.valueOf(MDC.get("start_time"));
			int ret_code = res.getStatus();
			String control_name = MDC.get("control_name");

			hashMap.put("res_str", resStr);
			hashMap.put("exe_time", exe_time);
			hashMap.put("end_time", end);
			hashMap.put("ret_code", ret_code);
			hashMap.put("control_name", control_name);

			MDC.put("res_str", resStr);
			MDC.put("exe_time", exe_time + "");
			MDC.put("end_time", end + "");
			MDC.put("ret_code", ret_code + "");

			if (logHandler != null) {
				logHandler.execute(hashMap);
			}
			logger.info(".");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("response拦截失败,原因:{}", e.getMessage(), e);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

	}

	public LogHandler getLogHandler() {
		return logHandler;
	}

	public void setLogHandler(LogHandler logHandler) {
		this.logHandler = logHandler;
	}

	public UrlInterceptor getUrlInterceptor() {
		return urlInterceptor;
	}

	public void setUrlInterceptor(UrlInterceptor urlInterceptor) {
		this.urlInterceptor = urlInterceptor;
	}

	@Override
	public void requestFilter(WrapperedRequest req, WrapperedResponse res) {

	}
	@Override
	public int getOrder() {

		return 1;
	}

	public FilterConfigurer getFilterConfigurer() {
		return filterConfigurer;
	}

	public void setFilterConfigurer(FilterConfigurer filterConfigurer) {
		this.filterConfigurer = filterConfigurer;
	}
}
