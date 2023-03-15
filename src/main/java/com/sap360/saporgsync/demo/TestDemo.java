package com.sap360.saporgsync.demo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author RawChen
 * @date 2023-03-07 11:29
 */
public class TestDemo {
	public static void main(String[] args) {

		String format = LocalDateTime.now().
				format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+08:00"));
		System.out.println(format);
	}
}
