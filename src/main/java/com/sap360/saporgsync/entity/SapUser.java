package com.sap360.saporgsync.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class SapUser {
    @JSONField(name = "lastName")
    private String lastName;
    @JSONField(name = "EnglishName")
    private String englishName;
    @JSONField(name = "mobile")
    private String mobile;
    @JSONField(name = "DocEntry")
    private String docEntry;
}
