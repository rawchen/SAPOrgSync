package com.sap360.saporgsync.dao.impl;

import com.sap360.saporgsync.dao.DepartmentDao;
import com.sap360.saporgsync.entity.Department;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-06 14:09
 */
@Component
public class DepartmentDaoImpl implements DepartmentDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Department> findall() {
//		return jdbcTemplate.query("select * from department", new Object[]{}, new BeanPropertyRowMapper<Department>(Department.class));
		return Collections.emptyList();
	}

	@Override
	public Department findById(String id) {
		Department department = null;
		try {
			department = jdbcTemplate.queryForObject("select * from department where id = ?", new BeanPropertyRowMapper<Department>(Department.class), id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return department;
	}

	@Override
	public boolean add(Department department) {
		String sql = "insert into department(id, feishu_id, feishu_parent_id, sap_id, sap_parent_id, name, doc_entry) value (?,?,?,?,?,?)";
		jdbcTemplate.update(sql, department.getId(), department.getFeishuId(), department.getFeishuParentId(),
				department.getSapId(), department.getSapParentId(),
				department.getName(), department.getDocEntry());
		return true;
	}

	@Override
	public boolean addList(List<Department> departments) {

		jdbcTemplate.batchUpdate("insert into department(id, feishu_id, feishu_parent_id, sap_id, sap_parent_id, name, doc_entry) values (?,?,?,?,?,?,?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
//						ps.setString(1, departments.get(i).getId());
//						ps.setString(2, departments.get(i).getParentId());
						ps.setString(3, departments.get(i).getSapId());
						ps.setString(4, departments.get(i).getSapParentId());
						ps.setString(5, departments.get(i).getName());
						ps.setString(6, departments.get(i).getDocEntry());
					}

					@Override
					public int getBatchSize() {
						return departments.size();
					}
				});
		return true;
	}

	@Override
	public boolean updateById(Department department) {
		return false;
	}

	@Override
	public boolean deleteById(String id) {
		return false;
	}

	@Override
	public int count() {
		Integer integer = jdbcTemplate.queryForObject("select count(1) from department", Integer.class);
		if (integer != null) {
			return integer;
		} else {
			return 0;
		}
	}
}
