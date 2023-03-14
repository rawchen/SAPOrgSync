package com.sap360.saporgsync.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author RawChen
 * @date 2023-03-13 13:42
 */
@Data
@Document(collection = "销售订单")
public class Order {

	@Field("_id")
	private String ID;

	@Field("客户代码")
	private String customerCode;

	@Field("客户名称")
	private String customerName;

	@Field("单据日期")
	private Date documentDate;

	@Field("销售部门代码")
	private String saleDeptCode;

	@Field("销售员代码")
	private Integer sellerCode;

	@Field("制单人代码")
	private Integer documentMakerCode;

	@Field("型号")
	private String modelName;

	@Field("品牌")
	private String brandName;

	@Field("仓库")
	private String warehouseName;

	@Field("含税单价")
	private Double unitPrice;

	@Field("未税单价")
	private Double unitPriceWithoutVAT;

	@Field("汇率")
	private Double exchangeRate;

	@Field("货币")
	private String currency;

	@Field("订单号")
	private String orderNumber;

	@Field("首次交易")
	private String firstTransaction;

	@Field("订单类型")
	private String orderType;

	@Field("数量")
	private Integer quantity;

	@Field("含税金额(交易货币)（W）")
	private Double taxIncludedAmount;

	@Field("含税金额(RMB)（W）")
	private Double taxIncludedAmountRMB;

	@Field("未税金额(RMB)（W）")
	private Double untaxedAmount;

	@Field("实际未税金额(RMB)（W）")
	private Double actualUntaxedAmount;

	@Field("客户性质")
	private String charaterOfCustomer;

	@Field("是否为KA客户")
	private String isKACustomer;

}
