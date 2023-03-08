package com.sap360.saporgsync.demo;

import com.sap360.saporgsync.util.SignUtil;

/**
 * @author RawChen
 * @date 2023-03-07 11:29
 */
public class Test {
	public static void main(String[] args) {
		String accessToken = SignUtil.getAccessToken();

		String result = SignUtil.getDepartmentIdAndName(accessToken, "od-b66de0fbb2edb71b7f5b020d675a3e04");
		System.out.println(result);
	}
}
