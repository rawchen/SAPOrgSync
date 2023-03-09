package com.sap360.saporgsync.util;

/**
 * @author RawChen
 * @date 2023-03-03 17:44
 */
public class StringUtil {

	/**
	 * 部门转换
	 * 1正式 2实习 3外包 4劳务 5顾问 6离职 7试用
	 * A:正式 B:离职 C:试用期
	 *
	 * 飞书管理后台->组织架构->成员字段管理
	 * 需要新增自定义人员类型字段离职和试用
	 *
	 * @param employeeType
	 * @return
	 */
	public static String employeeConvert(String employeeType) {
		if (employeeType == null) {
			return "A";
		} else if ("1".equals(employeeType)) {
			return "A";
		} else if ("2".equals(employeeType)) {
			return "C";
		} else if ("6".equals(employeeType)) {
			return "B";
		}
		return "A";
	}

	/**
	 * 去掉飞书返回的手机号国际字冠+86
	 *
	 * @param mobile
	 * @return
	 */
	public static String mobileDivAreaCode(String mobile) {
		if (mobile == null) {
			return "";
		} else if (mobile.startsWith("+86")) {
			return mobile.substring(3);
		} else {
			return mobile;
		}
	}
}
