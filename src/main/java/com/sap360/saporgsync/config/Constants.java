package com.sap360.saporgsync.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RawChen
 * @date 2023-03-02 14:52
 */
public class Constants {

	// 飞书自建应用 App ID
	public final static String APP_ID_FEISHU = "cli_a483c4fd3ffc900e";

	// 飞书自建应用 App Secret
	public final static String APP_SECRET_FEISHU = "sahrtYO8zj49qHL6fzDtEgypSUl4YnHm";

	// 飞书自建应用订阅事件 Verification Token
	public final static String VERIFICATION_TOKEN = "KwIMEjPLCzY0RFdFhHzRzc86K2S0D4gN";

	// 飞书自建应用订阅事件 Encrypt Key
	public final static String ENCRYPT_KEY = "kJKZxCzb3v6LZJO3r8oESfSNN8f6x6o3";

	// MongoDB多维表格同步 App ID
	public final static String APP_ID_MONGO = "cli_a48407b1683f500c";

	// MongoDB多位表格同步 App Secret
	public final static String APP_SECRET_MONGO = "Pa0MzCnwvmv9Djn66JbTafvEKoWfBU7k";

	// 域名端口
	public final static String DOMAIN_PORT = "http://192.168.16.97:8059";

	// 应用ID
	public final static String APPID = "33461238";

	// 公司ID
	public final static String COMPANYID = "100000";

	// 安全码
	public final static String SECRETKEY = "624728dd116f45648ae91715a9b5b306";

	// formID 添加成员接口单据对象
	public final static String FORM_ID_USER = "-9644";

	// formID 组织架构（部门）
	public final static String FORM_ID_DEPT = "-96134";

	// queryId 部门查询ID
	public final static String QUERY_ID_DEPT = "3069";

	// queryId 用户查询ID
	public final static String QUERY_ID_USER = "3068";

	// 添加接口
	public final static String ADD = "/OpenAPI/Company/Document/V1/Add/" + APPID + "/" + COMPANYID;

	// 修改接口
	public final static String UPDATE = "/OpenAPI/Company/Document/V1/Update/" + APPID + "/" + COMPANYID;

	// 查找列表接口
	public final static String QUERY = "/OpenApi/Company/QueryData/V1/Query/" + APPID + "/" + COMPANYID;

	// MongoDB多维表格同步 多维表格 bitable app token
	public final static String APP_TOKEN = "bascnWZ508kPtmZFDCHZQU6tYwe";

	// MongoDB多维表格同步 数据表ID 销售订单
	public final static String TABLE_ID_ORDER = "tblQT7GoEofmlOEd";

	// MongoDB多维表格同步 数据表ID 销售回款
	public final static String TABLE_ID_REFUND = "tblFrPAHfavwOxbK";

	// MongoDB多维表格同步 数据表ID 销售退货
	public final static String TABLE_ID_RETURNABLE = "tblSmRdy26Fyj1vS";

	// 销售订单内存记录IDS
	public static List<String> recordIdsOrder = new ArrayList<>();

	// 销售回款内存记录IDS
	public static List<String> recordIdsRefund = new ArrayList<>();

	// 销售退货内存记录IDS
	public static List<String> recordIdsReturnable = new ArrayList<>();

}
