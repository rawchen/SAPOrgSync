package com.sap360.saporgsync.dao.impl;

import com.sap360.saporgsync.dao.UserDao;
import com.sap360.saporgsync.entity.User;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-06 10:02
 */
@Service
public class UserDaoImpl implements UserDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<User> findall() {
		String sql = "select * from user";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<User>(User.class));
	}

	@Override
	public User findById(String id) {
		return null;
	}

	@Override
	public boolean add(User user) {
		return false;
	}

	@Override
	public boolean addList(List<User> users) {
		jdbcTemplate.batchUpdate("insert into user(id, name, sap_id, doc_entry, dept_id) values (?,?,?,?,?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, users.get(i).getId());
						ps.setString(2, users.get(i).getName());
						ps.setString(3, users.get(i).getSapId());
						ps.setString(4, users.get(i).getDocEntry());
						ps.setString(5, users.get(i).getDeptId());
					}
					@Override
					public int getBatchSize() {
						return users.size();
					}
				});
		return true;
	}

	@Override
	public boolean updateById(User user) {
		return false;
	}

	@Override
	public boolean deleteById(String id) {
		return false;
	}

	@Override
	public int count() {
		Integer integer = jdbcTemplate.queryForObject("select count(1) from user", Integer.class);
		if (integer != null) {
			return integer;
		} else {
			return 0;
		}
	}
}
