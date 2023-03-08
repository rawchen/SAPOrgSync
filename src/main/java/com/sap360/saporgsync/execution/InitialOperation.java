package com.sap360.saporgsync.execution;

import com.sap360.saporgsync.controller.EventController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Spring Boot启动后自动执行
 *
 * @author RawChen
 * @since 2023-03-07 15:50
 */
@Component
@Order(1)
public class InitialOperation implements CommandLineRunner {

	@Autowired
	private EventController eventController;

	@Override
	public void run(String... args) throws Exception {
		eventController.initDept();
		eventController.initUser();
	}
}
