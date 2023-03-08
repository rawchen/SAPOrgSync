package com.sap360.saporgsync;

import com.lark.oapi.sdk.servlet.ext.ServletAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SapOrgSyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SapOrgSyncApplication.class, args);
	}

	// 注入扩展实例到 IOC 容器
	@Bean
	public ServletAdapter getServletAdapter() {
		return new ServletAdapter();
	}

}
