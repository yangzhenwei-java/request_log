package com.github.log.filter.impl;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.github.log.exception.FilterException;
import com.github.log.filter.CommonFilter;
import com.github.log.filter.common.BeebanKFilterAware;
import com.github.log.filter.wrapper.WrapperedRequest;
import com.github.log.filter.wrapper.WrapperedResponse;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import org.springframework.util.StringUtils;

public class MDCFilter implements CommonFilter,BeebanKFilterAware {
	
	static String service_ip = "";

	static MessageDigest md5 ;
	private static final Logger logger = LoggerFactory.getLogger(MDCFilter.class);
	static {
			try {
				
				InetAddress addr = InetAddress.getLocalHost();
				service_ip = addr.getHostAddress();
			} catch (UnknownHostException e) {
				logger.error("获取本机IP失败,原因:{}",e.getMessage(),e);
				e.printStackTrace();
			}
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			logger.error("获取MD5失败,原因:{}",e.getMessage(),e);
			e.printStackTrace();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
	}



	@Override
	public void requestFilter(WrapperedRequest req, WrapperedResponse res) throws FilterException {

		try {
			HttpSession session = req.getSession();
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
			String session_id = "null" ;
			String user_no = "null" ;
			String user_name = "null" ;

			if(session!=null){
				session_id = session.getId().substring(0,5);
				user_no = String.valueOf(session.getAttribute("user_no"));
				user_name = String.valueOf(session.getAttribute("user_name")) ;
			}
			MDC.put("session_id",session_id);
			MDC.put("user_no",user_no);
			MDC.put("user_name",user_name);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("{}",e.getMessage(),e);
		}

	}


	@Override
	public void responseFilter(WrapperedRequest req,WrapperedResponse res) throws FilterException {
		
	}
	
	/**
	 * 跨过nginx获取客户端IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		String[] ips = ip.split(",");
		return ips[0];
	}

	@Override
	public int getOrder() {
		
		return 0;
	}


	public static String StringToMD5 (String str){
		if(StringUtils.isEmpty(str)){
			return str;
		}
		String s = "";
		try {
			byte[] bytes = md5.digest(str.getBytes("UTF-8"));
			s = Hex.encodeHexString(bytes);

		} catch (UnsupportedEncodingException e) {
			logger.error("str.getBytes(\"UTF-8\")失败,原因:{}",e.getMessage(),e);
			e.printStackTrace();
		}
		return s;
	}

}
