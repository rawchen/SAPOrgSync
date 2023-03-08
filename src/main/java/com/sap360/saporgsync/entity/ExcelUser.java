package com.sap360.saporgsync.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author RawChen
 * @date 2023-03-06 15:16
 */
@Data
public class ExcelUser implements Comparable<ExcelUser> {

	@ExcelProperty(index = 0)
	private String id;

	@ExcelProperty(index = 2)
	private String name;

	@ExcelProperty(index = 4)
	private String deptName;


	@Override
	public int compareTo(ExcelUser o) {
		String str = o.getDeptName().replace("/", "");
		if (deptName.length() > str.length()) {
			return 0;
		} else {
			return 1;
		}
	}
}
