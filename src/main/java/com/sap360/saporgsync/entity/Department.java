package com.sap360.saporgsync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 用户映射关系
 *
 * @author RawChen
 * @date 2023-03-06 9:56
 */
@Data
@Builder
@TableName("department")
public class Department {

	/**
	 * ID
	 */
	@TableId(type = IdType.AUTO)
	private long id;

	/**
	 * 飞书部门ID
	 */
	private String feishuId;

	/**
	 * 飞书父ID
	 */
	private String feishuParentId;

	/**
	 * 部门ID（SAP系统）
	 */
	private String sapId;

	/**
	 * 部门父ID（SAP系统）
	 */
	private String sapParentId;

	/**
	 * 部门名称
	 */
	private String name;

	/**
	 * 单据编号
	 */
	private String docEntry;

}
