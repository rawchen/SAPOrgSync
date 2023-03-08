package com.sap360.saporgsync.entity;

import lombok.Data;

/**
 * @author RawChen
 * @date 2023-03-06 15:16
 */
@Data
public class ExcelDept implements Comparable<ExcelDept> {

    private String id;
    private String name;
    private String parentId;


    @Override
    public int compareTo(ExcelDept o) {
        String str = o.getName().replace("/", "");
        return name.length() > str.length() ? 0 : 1;
    }
}
