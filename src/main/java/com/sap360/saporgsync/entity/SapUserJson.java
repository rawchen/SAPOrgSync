package com.sap360.saporgsync.entity;

import lombok.Data;

/**
 * @author RawChen
 * @date 2023-03-06 13:47
 */
@Data
public class SapUserJson {

	/**
	 * 飞书用户ID
	 */
	private String id;

	/**
	 * 飞书用户姓名
	 */
	private String name;

	/**
	 * 飞书部门名称
	 */
	private String deptName;

}
