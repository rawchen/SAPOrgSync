package com.sap360.saporgsync.dao;

import com.sap360.saporgsync.entity.Department;

import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-06 9:59
 */
public interface DepartmentDao {

	List<Department> findall();

	Department findById(String id);

	boolean add(Department department);

	boolean addList(List<Department> departments);

	boolean updateById(Department department);

	boolean deleteById(String id);

	int count();

	String getNameByParentId(String feishuParentId);
}
