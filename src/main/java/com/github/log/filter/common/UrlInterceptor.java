package com.github.log.filter.common;

import com.github.log.conf.MatchesRes;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;

/**
 * 此类用于根据访问的url来判断是否需要记录日志
 * Created by yzw on 16/7/28.
 */
public  class UrlInterceptor {

    //  需要记录日志的url规则
    private  String[] includePatterns;
    //    key为url地址   value为是否记录返回内容
    private Map<String,Boolean> includePatternsMap;
    //  不需要记录日志的url规则
    private  String[] excludePatterns;

    public UrlInterceptor(Map includePatternsMap, String[] excludePatterns) {
        this.includePatternsMap = includePatternsMap;
        this.excludePatterns = excludePatterns;
        if(includePatternsMap!=null){
            Set<String> set = includePatternsMap.keySet();
            includePatterns = new String[set.size()];
            Iterator<String> iterator = set.iterator();
            int i = 0;
            while(iterator.hasNext()){
                includePatterns[i] = iterator.next();
                i++;
            }

        }

    }

    //  关于Spring的AntPathMatcher(路径匹配)
    private PathMatcher pathMatcher = new AntPathMatcher();


    public MatchesRes matches(String lookupPath) {

        if (this.includePatterns == null) {
            return new MatchesRes(false,false);
        }

        boolean flag = false;
        boolean isRes = false;  // 是否记录返回值
        for (String pattern : this.includePatterns) {
            if (pathMatcher.match(pattern, lookupPath)) {
                flag = true;
                isRes = includePatternsMap.get(pattern);
                break;
            }
        }
        if(!flag){
            return new MatchesRes(false,false);
        }

        // 程序走到此处  表明includePatterns中的规则匹配url
        // 下面的流程表示需要排除的url
        if (this.excludePatterns != null) {
            for (String pattern : this.excludePatterns) {
                if (pathMatcher.match(pattern, lookupPath)) {
                    return new MatchesRes(false,false);
                }
            }
        }
        return new MatchesRes(true,isRes);
    }




}
