package com.github.log.filter.common;

import java.util.Comparator;

import com.github.log.filter.CommonFilter;

public class FilterComparator implements Comparator<CommonFilter> {

	@Override
	public int compare(CommonFilter o1, CommonFilter o2) {
		if(o1.getOrder()>o2.getOrder()){
			return 1;
		}else if(o1.getOrder()==o2.getOrder()){
			return 0;
		}else{
			return -1;
		}
	}

}
