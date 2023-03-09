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
        int count = o.getName().length() - str.length();

        String strThis = this.name.replace("/", "");
        int countThis = this.name.length() - strThis.length();

        if (count > countThis) {
            return -1;
        } else if (count < countThis) {
            return 1;
        } else {
            return 0;
        }
    }
}
