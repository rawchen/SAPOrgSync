package com.sap360.saporgsync.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sap360.saporgsync.config.Constants;
import com.sap360.saporgsync.entity.Department;
import com.sap360.saporgsync.entity.ExcelDept;
import com.sap360.saporgsync.entity.SapDept;
import com.sap360.saporgsync.mapper.DepartmentMapper;
import com.sap360.saporgsync.service.SystemService;
import com.sap360.saporgsync.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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

    @Override
    @Transactional
    public String initDepartment() {
        // 获取飞书部门列表
        List<ExcelDept> excelDeptList = parseExcel();
        // 查询联创杰部门列表
        List<SapDept> sapDeptList = queryDepartmentList();
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

    private List<SapDept> queryDepartmentList() {
        String currentPage = "0";
        JSONObject obj = new JSONObject();
        obj.put("DeptName", "综合一组");
        String requestJson = obj.toJSONString();
        String timestamp = TimeUtil.getTimestamp();
        StringBuilder url = new StringBuilder();

        Map<String, String> objects = new HashMap<>();
        objects.put("APPID", "33461238");
        objects.put("COMPANYID", "100001");
        objects.put("QUERYID", Constants.QUERY_ID_DEPT);
        objects.put("CURRENTPAGE", currentPage);
        objects.put("TIMESTAMP", timestamp);

        String md5Token = makeMd5Token(objects, Constants.SECRETKEY, requestJson);
        url.append(Constants.DOMAIN_PORT).append(Constants.LIST_DEPT)
                .append("/").append(Constants.QUERY_ID_DEPT)
                .append("/").append(currentPage)
                .append("/").append(timestamp)
                .append("/").append(md5Token);
        try {
            String s = HttpRequest.post(url.toString())
                    .body(requestJson).execute().body();
            JSONObject result = (JSONObject) JSONObject.parse(s);
            JSONArray resultArray = (JSONArray) result.get("Result");
            log.info(s);
            if (resultArray == null) {
                return Collections.emptyList();
            }
            return JSONObject.parseArray(resultArray.toJSONString(), SapDept.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public String initUser() {
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

    public static String makeMd5Token(Map<String, String> objects, String secretKey, String requestJson) {
        StringBuilder content = new StringBuilder();
        content.append(secretKey);
        // 对 resultmap 中的参数进行排序
        List<String> keyList = new ArrayList<>();
        Iterator<Map.Entry<String, String>> ite = objects.entrySet().iterator();
        while (ite.hasNext()) {
            keyList.add(ite.next().getKey());
        }
        Collections.sort(keyList);
        // 拼接 secretKey
        for (String key : keyList) {
            content.append(key).append(objects.get(key));
        }
        content.append(requestJson).append(secretKey);
        // 生成 md5 签名
        return DigestUtils.md5Hex(content.toString());
    }
}
