package com.sap360.saporgsync.service;

/**
 * @author RawChen
 * @date 2023-03-13 9:44
 */
public interface MongoSyncService {

	void syncDataOrder();

	void syncDataRefund();

	void syncDataReturnable();
}
