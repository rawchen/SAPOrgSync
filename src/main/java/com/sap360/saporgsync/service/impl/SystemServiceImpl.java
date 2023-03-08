package com.sap360.saporgsync.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sap360.saporgsync.entity.Department;
import com.sap360.saporgsync.entity.ExcelDept;
import com.sap360.saporgsync.entity.SapDept;
import com.sap360.saporgsync.mapper.DepartmentMapper;
import com.sap360.saporgsync.service.DeptService;
import com.sap360.saporgsync.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {
    private static final String DEPT_FILE_NAME = "部门信息.xlsx";

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DeptService deptService;

    @Override
    @Transactional
    public String initDepartment() {
        // 获取飞书部门列表
        List<ExcelDept> excelDeptList = parseExcel();
        // 查询联创杰部门列表
        List<SapDept> sapDeptList = deptService.queryDepartmentList();
        // 构建部门映射关系
        if (CollectionUtil.isNotEmpty(excelDeptList) && CollectionUtil.isNotEmpty(sapDeptList)) {
            Map<String, List<ExcelDept>> fsGroup = excelDeptList.stream().collect(Collectors.groupingBy(ExcelDept::getName));
            Map<String, List<SapDept>> sapGroup = sapDeptList.stream().collect(Collectors.groupingBy(SapDept::getName));
            // ExcelDept 与 SapDept 合并为 Department
            List<Department> departments = fsGroup.entrySet().stream()
                    .map(dept -> {
                        String name = dept.getKey();
                        ExcelDept excelDept = dept.getValue().get(0);
                        String sapDeptId = null;
                        String sapParentId = null;
                        String docEntry = null;
                        if (StrUtil.isNotBlank(name)) {
                            List<SapDept> list = sapGroup.get(name);
                            if (CollectionUtil.isNotEmpty(list)) {
                                SapDept sapDept = list.get(0);
                                sapDeptId = sapDept.getId();
                                sapParentId = sapDept.getParentId();
                                docEntry = sapDept.getDocEntry();
                            }
                        }
                        return Department.builder()
                                .name(excelDept.getName())
                                .feishuDeptId(excelDept.getId())
                                .feishuParentId(excelDept.getParentId())
                                .sapDeptId(sapDeptId)
                                .sapParentId(sapParentId)
                                .docEntry(docEntry)
                                .build();
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(departments)) {
                departmentMapper.insertBatch(departments);
            }
        }
        return "success";
    }

    @Override
    public String initUser() {
        // 初始化用户
        // 正式库开始存在用户
        return "success";
    }

    private List<ExcelDept> parseExcel() {
        List<ExcelDept> departments = new ArrayList<>();
        try {
            ClassPathResource classPathResource = new ClassPathResource(DEPT_FILE_NAME);
            EasyExcel.read(classPathResource.getInputStream(), ExcelDept.class, new AnalysisEventListener<ExcelDept>() {
                @Override
                public void invoke(ExcelDept dept, AnalysisContext context) {
                    departments.add(dept);
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            })
                    .sheet()
                    .headRowNumber(2)
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 处理部门父子关系
        for (ExcelDept dept : departments) {
            if (dept.getName().contains("|")) {
                dept.setName(dept.getName().substring(0, dept.getName().indexOf("|")));
            }
            dept.setName(dept.getName().substring(dept.getName().indexOf("/")));
            // 过滤完如果只有斜杠则为第一级，设置parentId为sap系统的10
            String str = dept.getName().replace("/", "");
            if (dept.getName().length() - str.length() == 1) {
                dept.setParentId("10");
            }
        }
        // ExcelDept 根据/数量少到多排序
        List<ExcelDept> newDepartments = departments.stream()
                .sorted(Comparator.comparing(ExcelDept::getName))
                .collect(Collectors.toList());
        // 递归找 parentID
        matchParentId(newDepartments);
        // 去除多余层级
        for (ExcelDept newExcelDept : newDepartments) {
            String temp;
            temp = newExcelDept.getName().substring(newExcelDept.getName().lastIndexOf("/") + 1);
            newExcelDept.setName(temp);
        }
        return newDepartments;
    }

    /**
     * 匹配层级关系
     *
     * @param departments 部门列表
     */
    private void matchParentId(List<ExcelDept> departments) {
        for (int i = 0; i < departments.size(); i++) {
            // 如果斜杠大于一个就需要去找父ID
            if (departments.get(i).getName().length() - departments.get(i).getName().replace("/", "").length() > 1) {
                // 往上找斜杠数量与之不同的，就是它的parent
                int tempIndex = i;
                while (tempIndex > 0) {
                    int lengthThis = departments.get(i).getName().length() - departments.get(i).getName().replace("/", "").length();
                    int lengthLast = departments.get(tempIndex - 1).getName().length() - departments.get(tempIndex - 1).getName().replace("/", "").length();
                    if (lengthThis != lengthLast) {
                        // 如果斜杠数量不同就说明是父级
                        departments.get(i).setParentId(departments.get(tempIndex - 1).getId());
                        break;
                    } else {
                        tempIndex--;
                    }
                }
            }
        }
    }


}
