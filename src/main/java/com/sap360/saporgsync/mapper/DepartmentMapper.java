package com.sap360.saporgsync.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sap360.saporgsync.entity.Department;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-08 10:10
 */
@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

	default List<Department> selectAll() {
		return selectList(new LambdaQueryWrapper<>());
	}

	void insertBatch(List<Department> departments);

	default long count() {
		return selectCount(new LambdaQueryWrapper<>());
	}
}
