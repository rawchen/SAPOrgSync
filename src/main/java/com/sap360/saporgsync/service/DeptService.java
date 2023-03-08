package com.sap360.saporgsync.service;

import com.sap360.saporgsync.entity.SapDept;

import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-08 19:53
 */
public interface DeptService {

	/**
	 * 获取SAP部门列表
	 *
	 * @return
	 */
	List<SapDept> queryDepartmentList();
}
