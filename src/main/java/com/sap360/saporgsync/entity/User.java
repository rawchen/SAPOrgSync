package com.sap360.saporgsync.entity;

import lombok.Data;

/**
 * 用户映射关系
 *
 * @author RawChen
 * @date 2023-03-06 9:56
 */
@Data
public class User {

	/**
	 * 飞书ID
	 */
	private String id;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 用户ID（SAP系统）
	 */
	private String sapId;

	/**
	 * SAP单据编号
	 */
	private String docEntry;

	/**
	 * 飞书部门ID
	 */
	private String deptId;
}
