package com.sap360.saporgsync.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap360.saporgsync.config.Constants;
import com.sap360.saporgsync.entity.SapDept;
import com.sap360.saporgsync.service.DeptService;
import com.sap360.saporgsync.util.SignUtil;
import com.sap360.saporgsync.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RawChen
 * @date 2023-03-08 19:52
 */
@Slf4j
@Service
public class DeptServiceImpl implements DeptService {

	/**
	 * 获取SAP部门列表
	 *
	 * @return
	 */
	public List<SapDept> queryDepartmentList() {
		String currentPage = "0";
		String requestJson = "{}";
		String timestamp = TimeUtil.getTimestamp();
		StringBuilder url = new StringBuilder();

		Map<String, String> objects = new HashMap<>();
		objects.put("APPID", Constants.APPID);
		objects.put("COMPANYID", Constants.COMPANYID);
		objects.put("QUERYID", Constants.QUERY_ID_DEPT);
		objects.put("CURRENTPAGE", currentPage);
		objects.put("TIMESTAMP", timestamp);

		String md5Token = SignUtil.makeMd5Token(objects, Constants.SECRETKEY, requestJson);
		url.append(Constants.DOMAIN_PORT).append(Constants.QUERY)
				.append("/").append(Constants.QUERY_ID_DEPT)
				.append("/").append(currentPage)
				.append("/").append(timestamp)
				.append("/").append(md5Token);
		try {
			String s = HttpRequest.post(url.toString())
					.body(requestJson).execute().body();
			JSONObject result = (JSONObject) JSONObject.parse(s);
			JSONArray resultArray = (JSONArray) result.get("Result");
			if (resultArray == null) {
				return Collections.emptyList();
			}
			return JSONObject.parseArray(resultArray.toJSONString(), SapDept.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
