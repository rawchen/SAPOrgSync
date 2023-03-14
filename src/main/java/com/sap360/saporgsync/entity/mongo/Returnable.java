package com.sap360.saporgsync.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author RawChen
 * @date 2023-03-13 11:32
 */
@Data
@Document(collection = "销售退货")
public class Returnable {

	@Field("_id")
	private String ID;

	@Field("退货时间")
	private Date returnTime;

	@Field("退货原因")
	private String returnReason;

	@Field("质量问题")
	private String qualityProblem;

	@Field("所属销售员编号")
	private String salesNumber;

	@Field("退货数量")
	private Integer returnedQuantity;

	@Field("含税单价")
	private Double unitPrice;

	@Field("未税单价")
	private Double untaxedPrice;

	@Field("含税金额(交易货币)")
	private Double unitAmount;

	@Field("未税金额(交易货币)")
	private Double untaxedAmount;

	@Field("含税金额(RMB)")
	private Double unitAmountRmb;

	@Field("未税金额(RMB)")
	private Double untaxedAmountRmb;

	@Field("含税金额(交易货币)/W")
	private Double unitAmountMyriad;

	@Field("未税金额(交易货币)/W")
	private Double untaxedAmountMyriad;

	@Field("含税金额(RMB)/W")
	private Double unitAmountRmbMyriad;

	@Field("未税金额(RMB)/W")
	private Double untaxedAmountRmbMyriad;

	@Field("型号")
	private String model;

	@Field("品牌")
	private String brand;

	@Field("部门编号")
	private String deptNumber;

}
