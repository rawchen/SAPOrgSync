package com.sap360.saporgsync;

import com.sap360.saporgsync.entity.SapUser;
import com.sap360.saporgsync.service.UserService;
import com.sap360.saporgsync.service.impl.UserServiceImpl;
import com.sap360.saporgsync.util.SignUtil;
import com.sap360.saporgsync.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SapOrgSyncApplicationTests {

	@Test
	void contextLoads() {
		String s = TimeUtil.timestampToUTC("1677813905");
		System.out.println(s);
	}

	@Test
	void getParentId() {
		String accessToken = SignUtil.getAccessToken();

		String result = SignUtil.getDepartmentIdAndName(accessToken, "od-b66de0fbb2edb71b7f5b020d675a3e04");
		System.out.println(result);

		UserService userService = new UserServiceImpl();
		List<SapUser> sapUsers = userService.queryUserList();
		for (SapUser sapUser : sapUsers) {
			System.out.println(sapUser);
		}
	}

}
