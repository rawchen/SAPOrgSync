package com.sap360.saporgsync.demo;

/**
 * @author RawChen
 * @date 2023-03-07 11:29
 */
public class Test {
	public static void main(String[] args) {
		String t = "/开发/后端开发/后端研发开发";
		String substring = t.substring(t.lastIndexOf("/") + 1);
		System.out.println(substring);
	}
}
