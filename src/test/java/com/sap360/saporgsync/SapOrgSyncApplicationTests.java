package com.sap360.saporgsync;

import com.sap360.saporgsync.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SapOrgSyncApplicationTests {

	@Test
	void contextLoads() {
		String s = TimeUtil.timestampToUTC("1677813905");
		System.out.println(s);
	}

}
