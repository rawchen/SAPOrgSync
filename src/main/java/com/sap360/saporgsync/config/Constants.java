package com.sap360.saporgsync.config;

/**
 * @author RawChen
 * @date 2023-03-02 14:52
 */
public class Constants {

	// 飞书自建应用 App ID
	public final static String APP_ID_FEISHU = "cli_a48aeeb160f8d00d";

	// 飞书自建应用 App Secret
	public final static String APP_SECRET_FEISHU = "o217Zu1yiRZhPduUnCkyPehVNPpAM1OE";

	// 飞书自建应用订阅事件 Verification Token
	public final static String VERIFICATION_TOKEN = "3d0xAnMEPdET3h4UVL8GvcwybNO2cHwk";

	// 飞书自建应用订阅事件 Encrypt Key
	public final static String ENCRYPT_KEY = "wf57O8PbcrpKXR2dfLUXFg4o5MZy8p2u";

	// 域名端口
	public final static String DOMAIN_PORT = "http://116.6.232.123:8059";

	// 应用ID
	public final static String APPID = "33461238";

	// 公司ID
	public final static String COMPANYID = "100001";

	// 安全码
	public final static String SECRETKEY = "624728dd116f45648ae91715a9b5b306";

	// formID 添加成员接口单据对象
	public final static String FORM_ID_USER = "-9644";

	// formID 组织架构（部门）
	public final static String FORM_ID_DEPT = "-96134";

	// queryId 部门查询ID
	public final static String QUERY_ID_DEPT = "3058";

	// 添加接口
	public final static String ADD = "/OpenAPI/Company/Document/V1/Add/" + APPID + "/" + COMPANYID;

	// 修改成员接口
	public final static String UPDATE_USER = "/OpenAPI/Company/Document/V1/Update/" + APPID + "/" + COMPANYID;

	// 查找部门列表接口
	public final static String LIST_DEPT = "/OpenApi/Company/QueryData/V1/Query/" + APPID + "/" + COMPANYID;

}
