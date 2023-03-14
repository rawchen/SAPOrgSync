package com.sap360.saporgsync.controller;

import com.sap360.saporgsync.service.MongoSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RawChen
 * @date 2023-03-13 9:50
 */
@RestController
public class MongoSyncController {

	@Autowired
	MongoSyncService mongoSyncService;

	/**
	 * 销售回款
	 *
	 * @return
	 */
	@GetMapping("/mongoDBTest01")
	public void syncDataOrder() {
		mongoSyncService.syncDataOrder();
	}

	/**
	 * 销售回款
	 *
	 * @return
	 */
	@GetMapping("/mongoDBTest02")
	public void syncDataRefund() {
		mongoSyncService.syncDataRefund();
	}

	/**
	 * 销售退货
	 *
	 * @return
	 */
	@GetMapping("/mongoDBTest03")
	public void syncDataReturnable() {
		mongoSyncService.syncDataReturnable();
	}

}
