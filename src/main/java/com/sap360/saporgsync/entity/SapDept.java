package com.sap360.saporgsync.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SapDept {
    @JSONField(name = "DeptCode")
    private String id;
    @JSONField(name = "DeptName")
    private String name;
    @JSONField(name = "ParentCode")
    private String parentId;
    @JSONField(name = "DocEntry")
    private String docEntry;
}
