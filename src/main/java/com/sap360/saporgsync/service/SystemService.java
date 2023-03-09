package com.sap360.saporgsync.service;

import com.sap360.saporgsync.entity.ExcelDept;

import java.util.List;

public interface SystemService {

    String initDepartment();

    String initUser();

    List<ExcelDept> parseExcel();

}
