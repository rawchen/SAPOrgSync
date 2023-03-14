package com.sap360.saporgsync.entity.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author RawChen
 * @date 2023-03-13 13:50
 */
@Data
@Document(collection = "销售回款")
public class Refund {

	@Field("_id")
	private String ID;

	@Field("销售员编号")
	private String sellerNumber;

	@Field("销售部门编号")
	private String saleDeptNumber;

	@Field("日期")
	private Date date;

	@Field("回款金额(W)")
	private Double paymentAmount;

	@Field("回款毛利(W)")
	private Double paymentGrossMargin;

	@Field("成本金额(W)")
	private Double costAmount;

	@Field("VIP客户回款金额(W)")
	private Double customerPaymentAmount;

}
