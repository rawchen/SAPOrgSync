package com.sap360.saporgsync.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sap360.saporgsync.entity.*;
import com.sap360.saporgsync.mapper.DepartmentMapper;
import com.sap360.saporgsync.mapper.UserMapper;
import com.sap360.saporgsync.service.DeptService;
import com.sap360.saporgsync.service.SystemService;
import com.sap360.saporgsync.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemServiceImpl implements SystemService {
    private static final String DEPT_FILE_NAME = "部门信息.xlsx";

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private UserService userService;

    /**
     * 初始化部门映射
     *
     * @return
     */
    @Override
    @Transactional
    public String initDepartment() {
        // 初始化部门映射，只初始化一次
        long number = departmentMapper.selectCount(null);
        if (number > 0) {
            return "No synchronization";
        }
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

    /**
     * 初始化用户映射
     *
     * @return
     */
    @Override
    @Transactional
    public String initUser() {
        // 初始化用户映射，只初始化一次
        long number = userMapper.selectCount(null);
        if (number > 0) {
            return "No synchronization";
        }

        // 飞书用户列表查询
        List<ExcelUser> excelUsers = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("通讯录-导出.xlsx");
        try {
            EasyExcel.read(classPathResource.getInputStream(), ExcelUser.class, new AnalysisEventListener<ExcelUser>() {
                @Override
                public void invoke(ExcelUser user, AnalysisContext context) {
                    excelUsers.add(user);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                }
            }).sheet().headRowNumber(3).doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 处理部门逗号，有则取逗号分割第一个
        for (ExcelUser excelUser : excelUsers) {
            if (excelUser.getDeptName().contains(",")) {
                excelUser.setDeptName(excelUser.getDeptName().substring(0, excelUser.getDeptName().indexOf(",")));
            }
        }

        // 处理飞书导入的部门名
        for (ExcelUser excelUser : excelUsers) {
            if (excelUser.getDeptName().contains("|")) {
                excelUser.setDeptName(excelUser.getDeptName().substring(0, excelUser.getDeptName().indexOf("|")));
            }
            excelUser.setDeptName(excelUser.getDeptName().substring(excelUser.getDeptName().indexOf("/")));
        }

        // 处理名称带有|EN-xxx的
        for (ExcelUser excelUser : excelUsers) {
            if (excelUser.getName().contains("|")) {
                excelUser.setName(excelUser.getName().substring(0, excelUser.getName().indexOf("|")));
            }
            excelUser.setDeptName(excelUser.getDeptName().substring(excelUser.getDeptName().indexOf("/")));
        }

        // 去除多余层级
        for (ExcelUser newExcelUser : excelUsers) {
            String temp;
            temp = newExcelUser.getDeptName().substring(newExcelUser.getDeptName().lastIndexOf("/") + 1);
            newExcelUser.setDeptName(temp);
        }

        // 部门匹配
        List<Department> departments = departmentMapper.selectAll();
        List<User> users = new ArrayList<>();

        for (ExcelUser excelUser : excelUsers) {
            User user = new User();
//            user.setSapId(excelUser.getId());
            user.setName(excelUser.getName());
//            user.setId(excelUser.getId());
            user.setUserId(excelUser.getId());
            for (Department department : departments) {
                if (department.getName().equals(excelUser.getDeptName())) {
                    user.setDeptId(department.getFeishuDeptId());
                    break;
                }
            }

            // SAP系统对应的人匹配
            List<SapUser> sapUsers = userService.queryUserList();
            for (SapUser sapUser : sapUsers) {
                if (excelUser.getName().equals(sapUser.getLastName())) {
                    user.setDocEntry(sapUser.getDocEntry());
                    break;
                }
            }

            users.add(user);
        }
        // 添加到用户映射表
        userMapper.insertBatch(users);

        return "success";
    }

    public List<ExcelDept> parseExcel() {
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
            }).sheet().headRowNumber(2).doRead();
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
        Collections.sort(departments);

        // 递归找 parentID
        matchParentId(departments);

        // 去除多余层级
        for (ExcelDept newExcelDept : departments) {
            String temp;
            temp = newExcelDept.getName().substring(newExcelDept.getName().lastIndexOf("/") + 1);
            newExcelDept.setName(temp);
        }
        return departments;
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
                    String name = departments.get(i).getName();
                    String tempName = departments.get(tempIndex - 1).getName();
                    String sub = name.substring(0, name.lastIndexOf("/"));
                    if (tempName.equals(sub)) {
                        // 如果截取开头到最后一个/前一个字符，与上面name匹配则为
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
