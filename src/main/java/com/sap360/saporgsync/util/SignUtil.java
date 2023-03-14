package com.sap360.saporgsync.util;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap360.saporgsync.config.Constants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author RawChen
 * @date 2023-03-08 18:37
 */
public class SignUtil {

	/**
	 * SAP系统自定义签名规则
	 * @param objects
	 * @param secretKey
	 * @param requestJson
	 * @return
	 */
	public static String makeMd5Token(Map<String, String> objects, String secretKey, String requestJson) {
		StringBuilder content = new StringBuilder();
		content.append(secretKey);
		// 对 resultmap 中的参数进行排序
		List<String> keyList = new ArrayList<>();
		Iterator<Map.Entry<String, String>> ite = objects.entrySet().iterator();
		while (ite.hasNext()) {
			keyList.add(ite.next().getKey());
		}
		Collections.sort(keyList);
		// 拼接 secretKey
		for (String key : keyList) {
			content.append(key).append(objects.get(key));
		}
		content.append(requestJson).append(secretKey);
		// 生成 md5 签名
		return DigestUtils.md5Hex(content.toString());
	}

	/**
	 * 飞书自建应用获取tenant_access_token
	 */
	public static String getAccessToken(String appId, String appSecret) {
		JSONObject object = new JSONObject();
		object.put("app_id", appId);
		object.put("app_secret", appSecret);
		String resultStr = HttpRequest.post("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal")
				.form(object)
				.execute().body();
		if (StringUtils.isNotEmpty(resultStr)) {
			JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
			if (!"0".equals(resultObject.getString("code"))) {
				return "";
			} else {
				String tenantAccessToken = resultObject.getString("tenant_access_token");
				if (tenantAccessToken != null) {
					return tenantAccessToken;
				}
			}
		}
		return "";
	}

	/**
	 * 根据OPEN ID获取部门ID和部门名
	 *
	 * @param accessToken
	 * @param openDepartmentId
	 * @return
	 */
	public static String getDepartmentIdAndName(String accessToken, String openDepartmentId) {
		String resultStr = HttpRequest.get(
				"https://open.feishu.cn/open-apis/contact/v3/departments/"
						+ openDepartmentId
						+ "?department_id_type=open_department_id&user_id_type=user_id")
				.header("Authorization", "Bearer " + accessToken)
				.execute().body();
		if (StringUtils.isNotEmpty(resultStr)) {
			JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
			if (!"0".equals(resultObject.getString("code"))) {
				return "";
			} else {
				JSONObject data = (JSONObject) resultObject.get("data");
				JSONObject department = (JSONObject) data.get("department");
				String department_id = department.getString("department_id");
				String name = department.getString("name");
				return department_id + "," + name;
			}
		}
		return "";
	}

	/**
	 * 根据OPEN ID获取部门ID和部门名(已自动生成access token)
	 *
	 * @param openDepartmentId
	 * @return
	 */
	public static String getDepartmentIdAndName(String openDepartmentId) {
		String accessToken = getAccessToken(Constants.APP_ID_FEISHU, Constants.APP_SECRET_FEISHU);
		return getDepartmentIdAndName(accessToken, openDepartmentId);
	}

	/**
	 * 多维表格新增多条记录
	 *
	 * @param json
	 * @return
	 */
	public static List<String> batchInsertRecord(String json, String appToken, String tableId) {
		String accessToken = getAccessToken(Constants.APP_ID_MONGO, Constants.APP_SECRET_MONGO);
		return batchInsertRecord(accessToken, json, appToken, tableId);
	}

	/**
	 * 多维表格新增多条记录
	 *
	 * @param accessToken
	 * @param json
	 * @return
	 */
	private static List<String> batchInsertRecord(String accessToken, String json, String appToken, String tableId) {
		List<String> recordIds = new ArrayList<>();
		String resultStr = HttpRequest.get("https://open.feishu.cn/open-apis/bitable/v1/apps/" + appToken + "/tables/" + tableId + "/records/batch_create")
				.header("Authorization", "Bearer " + accessToken)
				.body(json)
				.execute()
				.body();
		if (StringUtils.isNotEmpty(resultStr)) {
			JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
			if (!"0".equals(resultObject.getString("code"))) {
				System.out.println("批量插入失败：" + resultObject.getString("msg"));
				return new ArrayList<>();
			} else {
				JSONObject data = (JSONObject) resultObject.get("data");
				JSONArray records = (JSONArray) data.get("records");
				for (int i = 0; i < records.size(); i++) {
					JSONObject jsonObject = records.getJSONObject(i);
					String recordId = jsonObject.getString("record_id");
					recordIds.add(recordId);
				}
			}
		}
		return recordIds;
	}

	/**
	 * 批量根据内存记录的插入记录ids清空表格
	 *
	 * @param recordIds
	 * @param appToken
	 * @param tableId
	 */
	public static void batchClearTable(List<String> recordIds, String appToken, String tableId) {
		String accessToken = getAccessToken(Constants.APP_ID_MONGO, Constants.APP_SECRET_MONGO);
		batchClearTable(accessToken, recordIds, appToken, tableId);
	}

	/**
	 * 批量根据内存记录的插入记录ids清空表格
	 *
	 * @param accessToken
	 * @param recordIds
	 * @param appToken
	 * @param tableId
	 */
	public static void batchClearTable(String accessToken, List<String> recordIds, String appToken, String tableId) {
		System.out.println("===开始批量删除 " + tableId + "===");
		if (recordIds != null && recordIds.size() > 0) {
			List<List<String>> partitions = ListUtils.partition(recordIds, 500);
			for (List<String> partition : partitions) {
				JSONObject object = new JSONObject();
				object.put("records", JSONArray.parseArray(JSON.toJSONString(partition)));
				String resultStr = HttpRequest.get("https://open.feishu.cn/open-apis/bitable/v1/apps/" + appToken + "/tables/" + tableId + "/records/batch_delete")
						.header("Authorization", "Bearer " + accessToken)
						.body(object.toJSONString())
						.execute()
						.body();
				if (StringUtils.isNotEmpty(resultStr)) {
					JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
					if (!"0".equals(resultObject.getString("code"))) {
						System.out.println("批量删除失败：" + resultObject.getString("msg"));
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("===批量删除完成===");
	}
}
