package com.sap360.saporgsync.dao;

import com.sap360.saporgsync.entity.User;

import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-06 9:59
 */
public interface UserDao {

	List<User> findall();

	User findById(String id);

	boolean add(User user);

	boolean addList(List<User> users);

	boolean updateById(User user);

	boolean deleteById(String id);

	int count();
}
