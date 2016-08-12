package com.github.log.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.log.exception.FilterException;
import com.github.log.filter.common.FilterComparator;
import com.github.log.filter.wrapper.WrapperedRequest;
import com.github.log.filter.wrapper.WrapperedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FilterChain implements Filter {

	private static List<CommonFilter> filterChain = new ArrayList<CommonFilter>();

	// public final static ThreadLocal<HashMap<String,Object>> LOCAL = new
	// ThreadLocal<HashMap<String,Object>>();

	private static final Logger logger = LoggerFactory.getLogger(WrapperedRequest.class);

	private List<Pattern> ignoreUrl = new ArrayList<>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		String ignoreUrlSuffixsString = filterConfig.getInitParameter("ignoreUrlSuffixs");
		if (ignoreUrlSuffixsString != null) {
			String[] ignoreUrlSuffixsSplit = ignoreUrlSuffixsString.split(",");
			for (int i = 0; i < ignoreUrlSuffixsSplit.length; i++) {
                Pattern compile = Pattern.compile(ignoreUrlSuffixsSplit[i], Pattern.CASE_INSENSITIVE);
                ignoreUrl.add(compile);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, javax.servlet.FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		String requestURI = httpServletRequest.getRequestURI();
		if ((httpServletRequest.getContextPath() + "/").equals(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		String suffix = "-1";
		if (requestURI.lastIndexOf(".") != -1) {
			suffix = requestURI.substring(requestURI.lastIndexOf("."));
			suffix = suffix.toUpperCase().trim();
		}

		if (ignoreUrlSuffixsContains(suffix)) {
			chain.doFilter(request, response);
			return;
		}
		try {
			WrapperedRequest req = new WrapperedRequest(httpServletRequest);
			WrapperedResponse res = new WrapperedResponse((HttpServletResponse) response);
			for (int i = 0; i < filterChain.size(); i++) {
				CommonFilter commonFilter = filterChain.get(i);
				commonFilter.requestFilter(req, res);
			}
			try {
				chain.doFilter(req, res);
			} catch (Exception e) {
				logger.warn("chain.doFilter处理失败,原因{}", e.getMessage(), e);
				e.printStackTrace();
			} finally {
				for (int i = filterChain.size() - 1; i >= 0; i--) {
					CommonFilter commonFilter = filterChain.get(i);
					commonFilter.responseFilter(req, res);
				}
			}
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(res.getByteArray());
			outputStream.flush();
		} catch (FilterException e) {
			e.printStackTrace();
			logger.error("自定义FilterChain出错,原因{}", e.getMessage(), e);
		} finally {
			MDC.clear();
		}

	}

    /**
     *
     * @param suffix
     * @return
     */
    private boolean ignoreUrlSuffixsContains(String suffix) {
        for (Pattern p: ignoreUrl) {
            if(p.matcher(suffix).matches()){
                return true;
            }
        }
        return false;
    }

    @Override
	public void destroy() {

	}

	public static void addFilter(CommonFilter filter) {
		filterChain.add(filter);
	}

	public static void sort() {
		Collections.sort(filterChain, new FilterComparator());
	}

	public static void main(String[] args) {
        Pattern compile = Pattern.compile("\\.(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = compile.matcher(".html");
        System.out.println(matcher.matches());
    }

}
