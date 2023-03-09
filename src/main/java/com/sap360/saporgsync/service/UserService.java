package com.sap360.saporgsync.service;

import com.sap360.saporgsync.entity.SapUser;

import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-08 19:53
 */
public interface UserService {

	/**
	 * 获取SAP用户列表
	 *
	 * @return
	 */
	List<SapUser> queryUserList();
}
