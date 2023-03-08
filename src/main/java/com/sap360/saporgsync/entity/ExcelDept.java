package com.sap360.saporgsync.entity;

import lombok.Data;

/**
 * @author RawChen
 * @date 2023-03-06 15:16
 */
@Data
public class ExcelDept implements Comparable<ExcelDept> {

	private String id;

	private String content;

	private String parentID;


	@Override
	public int compareTo(ExcelDept o) {
		String str = o.getContent().replace("/", "");
		if (content.length() > str.length()) {
			return 0;
		} else {
			return 1;
		}
	}
}
