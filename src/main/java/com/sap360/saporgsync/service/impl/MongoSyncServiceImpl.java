package com.sap360.saporgsync.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap360.saporgsync.config.Constants;
import com.sap360.saporgsync.entity.mongo.Order;
import com.sap360.saporgsync.entity.mongo.Refund;
import com.sap360.saporgsync.entity.mongo.Returnable;
import com.sap360.saporgsync.service.MongoSyncService;
import com.sap360.saporgsync.util.SignUtil;
import com.sap360.saporgsync.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-13 9:44
 */
@Slf4j
@Service
public class MongoSyncServiceImpl implements MongoSyncService {

	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * 销售订单
	 *
	 * @return
	 */
	@Override
	@Scheduled(cron = "0 0 3 ? * *")
	public void syncDataOrder() {
		try {
			// 同步前完成表清空任务和内存的recordIds
			SignUtil.batchClearTable(Constants.recordIdsOrder, Constants.APP_TOKEN, Constants.TABLE_ID_ORDER);
			Constants.recordIdsOrder = new ArrayList<>();

			List<Order> orders;
			long count = mongoTemplate.count(new Query(), Order.class);
			if (count > 20000) {
				orders = mongoTemplate.find(new Query().skip(count - 20000), Order.class);
			} else {
				orders = mongoTemplate.findAll(Order.class);
			}

			System.out.println("===开始批量同步销售订单===");
			List<List<Order>> partitions = ListUtils.partition(orders, 500);
			for (List<Order> list : partitions) {
				String json = "";
				JSONObject body = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				for (Order field : list) {
					JSONObject object = new JSONObject();
					object.put("fields", JSONObject.toJSON(field));
					jsonArray.add(object);
				}
				body.put("records", jsonArray);
				json = body.toJSONString();
				// 处理字段为中文
				json = StringUtil.processChineseTitleOrder(json);
//				System.out.println(json);
				Constants.recordIdsOrder.addAll(SignUtil.batchInsertRecord(json, Constants.APP_TOKEN, Constants.TABLE_ID_ORDER));
				Thread.sleep(100);
			}
			System.out.println("===批量同步销售订单结束===");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 销售回款
	 *
	 * @return
	 */
	@Override
	@Scheduled(cron = "0 0 4 ? * *")
	public void syncDataRefund() {
		try {
			// 同步前完成表清空任务和内存的recordIds
			SignUtil.batchClearTable(Constants.recordIdsRefund, Constants.APP_TOKEN, Constants.TABLE_ID_REFUND);
			Constants.recordIdsRefund = new ArrayList<>();

			List<Refund> orders;
			long count = mongoTemplate.count(new Query(), Refund.class);
			if (count > 20000) {
				orders = mongoTemplate.find(new Query().skip(count - 20000), Refund.class);
			} else {
				orders = mongoTemplate.findAll(Refund.class);
			}

			System.out.println("===开始批量同步销售回款===");
			List<List<Refund>> partitions = ListUtils.partition(orders, 500);
			for (List<Refund> list : partitions) {
				String json = "";
				JSONObject body = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				for (Refund field : list) {
					JSONObject object = new JSONObject();
					object.put("fields", JSONObject.toJSON(field));
					jsonArray.add(object);
				}
				body.put("records", jsonArray);
				json = body.toJSONString();
				// 处理字段为中文
				json = StringUtil.processChineseTitleRefund(json);
//				System.out.println(json);
				Constants.recordIdsRefund.addAll(SignUtil.batchInsertRecord(json, Constants.APP_TOKEN, Constants.TABLE_ID_REFUND));
				Thread.sleep(100);
			}
			System.out.println("===批量同步销售回款结束===");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 销售退货
	 *
	 * @return
	 */
	@Scheduled(cron = "0 0 5 ? * *")
	// 测试定时：每分钟执行一次
//	@Scheduled(cron = "0 0/3 * * * ?")
	@Override
	public void syncDataReturnable() {
		try {
			// 同步前完成表清空任务和内存的recordIds
			SignUtil.batchClearTable(Constants.recordIdsReturnable, Constants.APP_TOKEN, Constants.TABLE_ID_RETURNABLE);
			Constants.recordIdsReturnable = new ArrayList<>();

			List<Returnable> orders;
			long count = mongoTemplate.count(new Query(), Returnable.class);
			if (count > 20000) {
				orders = mongoTemplate.find(new Query().skip(count - 20000), Returnable.class);
			} else {
				orders = mongoTemplate.findAll(Returnable.class);
			}

			System.out.println("===开始批量同步销售退货===");
			List<List<Returnable>> partitions = ListUtils.partition(orders, 500);
			for (List<Returnable> list : partitions) {
				String json = "";
				JSONObject body = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				for (Returnable field : list) {
					JSONObject object = new JSONObject();
					object.put("fields", JSONObject.toJSON(field));
					jsonArray.add(object);
				}
				body.put("records", jsonArray);
				json = body.toJSONString();
				// 处理字段为中文
				json = StringUtil.processChineseTitleReturnable(json);
//				System.out.println(json);
				Constants.recordIdsReturnable.addAll(SignUtil.batchInsertRecord(json, Constants.APP_TOKEN, Constants.TABLE_ID_RETURNABLE));
				Thread.sleep(100);
			}
			System.out.println("===批量同步销售退货结束===");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
