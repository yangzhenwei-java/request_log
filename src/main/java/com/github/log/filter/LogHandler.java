package com.github.log.filter;

import java.util.Map;

public interface LogHandler {
	
	/**
	 * 处理日志
	 * @param logMap
	 */
	public void execute(Map<String,Object> logMap );

}
