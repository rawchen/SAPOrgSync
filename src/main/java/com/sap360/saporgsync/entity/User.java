package com.sap360.saporgsync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户映射关系
 *
 * @author RawChen
 * @date 2023-03-06 9:56
 */
@Data
@TableName("user")
public class User {

	/**
	 * 飞书ID
	 */
	@TableId(type = IdType.AUTO)
	private long id;

	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 飞书用户ID
	 */
	private String userId;

	/**
	 * SAP单据编号
	 */
	private String docEntry;

	/**
	 * 飞书部门ID
	 */
	private String deptId;
}
