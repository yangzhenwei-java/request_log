package com.github.log.filter.impl;

import java.util.ArrayList;
import java.util.List;


import com.github.log.filter.UriStrategies;
import com.github.log.filter.wrapper.WrapperedResponse;
import org.springframework.aop.support.JdkRegexpMethodPointcut;


public class LogDefaultUriStrategies implements UriStrategies {



	@Override
	public byte[] calculateLogResContents(WrapperedResponse res) {
		
		List<String> list = new ArrayList<>();
		list.add("text/json".toUpperCase());
		list.add("text/javascript".toUpperCase());
		list.add("application/Json".toUpperCase());
		
		byte[] byteArray = res.getByteArray();
		if(res.getContentType()!=null&&list.contains(res.getContentType().toUpperCase())){
			return byteArray;
		}
		
		if(byteArray.length<=1024*20){
			return byteArray;
		}
		
		return null;
	}

	@Override
	public List<String> excludeUrls() {
		List<String> urls = new ArrayList<String>();
		urls.add("*/ShowImg");
		urls.add("*/uploadRealNameAuthImg");
		urls.add("*/updateUser");
		urls.add("*/upload");
		urls.add("*/norImgUpload");
		urls.add("*Export*");
		return urls;
	}
	
	public static void main(String[] args) {
//		Pattern compile = Pattern.compile(".*.Export.*");
//		Matcher matcher = compile.matcher("/service/postloanmanage/foreignTradeTrustExport");
//		System.out.println(matcher.matches());
//		String a = "*/ShowImg";
//		System.out.println(a.replace("*", ".*."));;



	}


}
