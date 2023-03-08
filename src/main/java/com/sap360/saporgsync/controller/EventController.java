package com.sap360.saporgsync.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.sdk.servlet.ext.ServletAdapter;
import com.lark.oapi.service.contact.v3.ContactService;
import com.lark.oapi.service.contact.v3.model.*;
import com.sap360.saporgsync.config.Constants;
import com.sap360.saporgsync.dao.UserDao;
import com.sap360.saporgsync.entity.Department;
import com.sap360.saporgsync.entity.User;
import com.sap360.saporgsync.entity.*;
import com.sap360.saporgsync.mapper.DepartmentMapper;
import com.sap360.saporgsync.util.StringUtil;
import com.sap360.saporgsync.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author RawChen
 * @date 2023-03-02 11:19
 */
@Slf4j
@RestController
@RequestMapping("api/")
public class EventController {

    @Autowired
    private ServletAdapter servletAdapter;

    @Autowired
    private UserDao userDao;
    @Autowired
    DepartmentMapper departmentMapper;

    // 注册消息处理器
    private final EventDispatcher EVENT_DISPATCHER = EventDispatcher
            .newBuilder(Constants.VERIFICATION_TOKEN, Constants.ENCRYPT_KEY)
            .onP2UserCreatedV3(new ContactService.P2UserCreatedV3Handler() {
                // 用户创建
                @Override
                public void handle(P2UserCreatedV3 event) {
                    log.info("P2UserCreatedV3: {}", Jsons.DEFAULT.toJson(event));
                    // 处理用户创建事件
                    // 1.获取处理订阅消息体
                    JSONObject requestJson = new JSONObject();
                    String resultJson = Jsons.DEFAULT.toJson(event);
                    JSONObject eventsObject = (JSONObject) JSONObject.parse(resultJson);
                    JSONObject eventObject = (JSONObject) eventsObject.get("event");
                    JSONObject object = (JSONObject) eventObject.get("object");
                    String user_id = object.getString("user_id");
                    String name = object.getString("name");
                    String en_name = object.getString("en_name");
                    String mobile = object.getString("mobile");
                    String email = object.getString("email");
                    JSONArray department_ids = (JSONArray) object.get("department_ids");
                    String employee_no = object.getString("employee_no");
                    String gender = object.getString("gender");
                    String city = object.getString("city");
                    String leader_user_id = object.getString("leader_user_id");
                    String employee_type = object.getString("employee_type");
                    String join_time = object.getString("join_time");
                    String job_title = object.getString("job_title");
                    String nickname = object.getString("nickname");

                    requestJson.put("ComCode", "4");
                    requestJson.put("lastName", name);
                    requestJson.put("EnglishName1", en_name);
                    requestJson.put("mobile", StringUtil.mobileDivAreaCode(mobile));
                    requestJson.put("email", email);
                    // 拿到飞书-SAP映射的部门id
                    log.info("department_ids: {}", department_ids.getString(0));
//                    Department byId = departmentDao.findById(department_ids.getString(0));
//                    if (byId != null && byId.getSapId() != null && !"".equals(byId.getSapId())) {
//                        requestJson.put("EmDept", byId.getSapId());
//                    } else {
//                        requestJson.put("EmDept", "");
//                    }

                    requestJson.put("JobNum", employee_no);
                    requestJson.put("sex", "1".equals(gender) ? "F" : "M");
                    requestJson.put("city", city);
                    requestJson.put("Leader", leader_user_id);
                    requestJson.put("Status", StringUtil.employeeConvert(employee_type));                    // A正式B离职C试用
                    requestJson.put("TimeOfEntry", TimeUtil.timestampToUTC(join_time));
                    requestJson.put("jobTitle", job_title);
                    requestJson.put("EnglishName", nickname);
                    requestJson.put("RowStatus", "A");
                    requestJson.put("DocEntry", "219");
                    String requestJsonAddArg = "{\"U_OHEM\":[" + requestJson.toJSONString() + "],}";

                    // 2.接口参数处理
                    String timestamp = TimeUtil.getTimestamp();
                    StringBuffer url = new StringBuffer();
                    Map<String, String> objects = new HashMap<>();
                    objects.put("APPID", Constants.APPID);
                    objects.put("COMPANYID", Constants.COMPANYID);
                    objects.put("TIMESTAMP", timestamp);
                    objects.put("FORMID", Constants.FORM_ID_USER);
//                    String md5Token = makeMd5Token(objects, Constants.SECRETKEY, requestJsonAddArg);
//                    url.append(Constants.DOMAIN_PORT).append(Constants.ADD)
//                            .append("/").append(Constants.FORM_ID_USER)
//                            .append("/").append(timestamp).append("/").append(md5Token);
                    // 3.调用SAP接口
                    try {
                        String resultStr = HttpRequest.post(url.toString())
                                .body(requestJsonAddArg)
                                .execute()
                                .body();
                        log.info("r: {}", resultStr);
                        if (StringUtils.isNotEmpty(resultStr)) {
                            JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
                            String resultCode = resultObject.getString("Code");
                            if (StringUtils.isNotEmpty(resultCode) && "0".equals(resultCode)) {
                                // 新增SAP用户成功后添加用户到映射表（包含DocEntry）
                                User user = new User();
                                user.setId(user_id);
                                user.setName(name);
                                user.setDocEntry(resultObject.getString("Result"));
                                user.setSapId(resultObject.getString("Result"));
                                userDao.add(user);

                                log.info("success: {}", resultStr);
                            } else {
                                log.info("fail: {}", resultStr);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).onP2UserUpdatedV3(new ContactService.P2UserUpdatedV3Handler() {
                // 用户修改
                @Override
                public void handle(P2UserUpdatedV3 event) throws Exception {
                    log.info("P2UserUpdatedV3: {}", Jsons.DEFAULT.toJson(event));
                    JSONObject requestJson = new JSONObject();
                    String resultJson = Jsons.DEFAULT.toJson(event);
                    JSONObject eventsObject = (JSONObject) JSONObject.parse(resultJson);
                    JSONObject eventObject = (JSONObject) eventsObject.get("event");
                    JSONObject object = (JSONObject) eventObject.get("object");
                    String user_id = object.getString("user_id");
                    String name = object.getString("name");
                    String en_name = object.getString("en_name");
                    String mobile = object.getString("mobile");
                    String email = object.getString("email");
                    JSONArray department_ids = (JSONArray) object.get("department_ids");
                    String employee_no = object.getString("employee_no");
                    String gender = object.getString("gender");
                    String city = object.getString("city");
                    String leader_user_id = object.getString("leader_user_id");
                    String employee_type = object.getString("employee_type");
                    String join_time = object.getString("join_time");
                    String job_title = object.getString("job_title");
                    String nickname = object.getString("nickname");

                    requestJson.put("ComCode", "4");
                    requestJson.put("lastName", name);
                    requestJson.put("EnglishName1", en_name);
                    requestJson.put("mobile", StringUtil.mobileDivAreaCode(mobile));
                    requestJson.put("email", email);
                    // 拿到飞书-SAP映射的部门id
//                    Department byId = departmentDao.findById(department_ids.getString(0));
//                    if (byId != null && byId.getSapId() != null && !"".equals(byId.getSapId())) {
//                        requestJson.put("EmDept", byId.getSapId());
//                    } else {
//                        requestJson.put("EmDept", "");
//                    }
                    requestJson.put("JobNum", employee_no);
                    requestJson.put("sex", "1".equals(gender) ? "F" : "M");
                    requestJson.put("city", city);
                    requestJson.put("Leader", leader_user_id);
                    requestJson.put("Status", StringUtil.employeeConvert(employee_type));                    // A正式B离职C试用
                    requestJson.put("TimeOfEntry", TimeUtil.timestampToUTC(join_time));
                    requestJson.put("jobTitle", job_title);
                    requestJson.put("EnglishName", nickname);
                    requestJson.put("RowStatus", "U");
                    requestJson.put("DocEntry", "219");
                    String requestJsonAddArg = "{\"U_OHEM\":[" + requestJson.toJSONString() + "],}";

                    // 2.接口参数处理
                    String timestamp = TimeUtil.getTimestamp();
                    StringBuffer url = new StringBuffer();
                    Map<String, String> objects = new HashMap<>();
                    objects.put("APPID", Constants.APPID);
                    objects.put("COMPANYID", Constants.COMPANYID);
                    objects.put("TIMESTAMP", timestamp);
                    objects.put("FORMID", Constants.FORM_ID_USER);
//                    String md5Token = makeMd5Token(objects, Constants.SECRETKEY, requestJsonAddArg);
//                    url.append(Constants.DOMAIN_PORT).append(Constants.UPDATE_USER)
//                            .append("/").append(Constants.FORM_ID_USER)
//                            .append("/").append(timestamp).append("/").append(md5Token);
                    // 3.调用SAP接口
                    try {
                        String resultStr = HttpRequest.post(url.toString())
                                .body(requestJsonAddArg)
                                .execute()
                                .body();
                        log.info("r: {}", resultStr);
                        if (StringUtils.isNotEmpty(resultStr)) {
                            JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
                            String resultCode = resultObject.getString("Code");
                            if (StringUtils.isNotEmpty(resultCode) && "0".equals(resultCode)) {
                                log.info("success: {}", resultStr);
                            } else {
                                log.info("fail: {}", resultStr);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).onP2UserDeletedV3(new ContactService.P2UserDeletedV3Handler() {
                // 用户删除
                @Override
                public void handle(P2UserDeletedV3 event) throws Exception {
                    log.info("P2UserDeletedV3: {}", Jsons.DEFAULT.toJson(event));
                }
            })
            .onP2DepartmentCreatedV3(new ContactService.P2DepartmentCreatedV3Handler() {
                // 部门创建
                @Override
                public void handle(P2DepartmentCreatedV3 event) throws Exception {
                    log.info("P2DepartmentCreatedV3: {}", Jsons.DEFAULT.toJson(event));
                }
            }).onP2DepartmentUpdatedV3(new ContactService.P2DepartmentUpdatedV3Handler() {
                // 部门修改
                @Override
                public void handle(P2DepartmentUpdatedV3 event) throws Exception {
                    log.info("P2DepartmentUpdatedV3: {}", Jsons.DEFAULT.toJson(event));
                }
            }).onP2DepartmentDeletedV3(new ContactService.P2DepartmentDeletedV3Handler() {
                // 部门删除
                @Override
                public void handle(P2DepartmentDeletedV3 event) throws Exception {
                    log.info("P2DepartmentDeletedV3: {}", Jsons.DEFAULT.toJson(event));
                }
            })
            .build();

    /**
     * 飞书订阅事件回调
     *
     * @param request
     * @param response
     * @throws Throwable
     */
    @RequestMapping(value = "/feishu/webhook/event")
    public void event(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        servletAdapter.handleEvent(request, response, EVENT_DISPATCHER);
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public List<User> test(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return userDao.findall();
    }

    /**
     * 初始化部门映射
     *
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/init/department")
    public String initDepartment() {
        // 如果SAP部门为空就向SAP系统插入所有飞书导出的部门列表
        List<Department> departments = new ArrayList<>();
//        if (sapDepts != null && sapDepts.size() == 0) {
//            for (ExcelDept dept : newExcelDepts) {
//                JSONObject requestJson = new JSONObject();
//                requestJson.put("DeptCode", dept.getId());
//                requestJson.put("Name", dept.getContent());
//                requestJson.put("ParentCode", dept.getParentID());
//                requestJson.put("U_MarketDept", "N");
//                requestJson.put("RowStatus", "A");
//                requestJson.put("DocEntry", 1);
//                String requestJsonAddArg = "{\n" +
//                        "    \"U_OEPT\":[\n" +
//                        "        {\n" +
//                        "            \"DocEntry\":1,\n" +
//                        "            \"RowStatus\":\"A\"\n" +
//                        "        }\n" +
//                        "    ],\n" +
//                        "    \"U_EPT1\":[\n" +
//                        "        {\n" +
//                        "            \"ParentName\":\"" + dept.getParentID() + "\" ,\n" +
//                        "            \"DeptName\":\"" + dept.getContent() + "\",\n" +
//                        "            \"LineNum\":1,\n" +
//                        "            \"RowStatus\":\"A\",\n" +
//                        "            \"DocEntry\":2\n" +
//                        "        }\n" +
//                        "    ]\n" +
//                        "}";
//
//                // 2.接口参数处理
//                String timestamp = TimeUtil.getTimestamp();
//                StringBuffer url = new StringBuffer();
//                Map<String, String> objects = new HashMap<>();
//                objects.put("APPID", Constants.APPID);
//                objects.put("COMPANYID", Constants.COMPANYID);
//                objects.put("TIMESTAMP", timestamp);
//                objects.put("FORMID", Constants.FORM_ID_DEPT);
//                String md5Token = makeMd5Token(objects, Constants.SECRETKEY, requestJsonAddArg);
//                url.append(Constants.DOMAIN_PORT).append(Constants.ADD)
//                        .append("/").append(Constants.FORM_ID_DEPT)
//                        .append("/").append(timestamp).append("/").append(md5Token);
//                // 3.调用SAP接口
//                try {
//                    System.out.println("url: " + url);
//                    String resultStr = HttpRequest.post(url.toString())
//                            .body(requestJsonAddArg)
//                            .execute()
//                            .body();
//                    if (StringUtils.isNotEmpty(resultStr)) {
//                        JSONObject resultObject = (JSONObject) JSON.parse(resultStr);
//                        String resultCode = resultObject.getString("Code");
//                        if (StringUtils.isNotEmpty(resultCode) && "0".equals(resultCode)) {
//                            // SAP部门同步成功后添加部门到映射表（包含DocEntry）
//                            Department department = Department.builder()
//                                    .name(dept.getContent())
//                                    .feishuDeptId(dept.getId())
//                                    .feishuParentId(dept.getParentID())
//                                    .sapDeptId()
//                                    .sapParentId(dept.getParentID())
//                                    .docEntry(resultObject.getString("Result"))
//                                    .build();
//                            departments.add(department);
//                            log.info("success: {}", resultStr);
//                        } else {
//                            log.info("fail: {}", resultStr);
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        // 根据部门名匹配
//        List<Department> departments = new ArrayList<>();
//        for (ExcelDept fs : newExcelDepts) {
//            for (SapDepartmentJson sap : sapDepts) {
//                // 系统运行前需保证两边系统的部门完全一致
//                if (sap.getName().equals(fs.getContent())) {
//                    Department department = new Department();
//                    department.setId(fs.getId());
//                    department.setParentId(fs.getParentID());
//                    department.setSapId(sap.getDeptCode());
//                    department.setSapParentId(sap.getParentCode());
//                    department.setName(sap.getName());
//                    departments.add(department);
//                    break;
//                }
//            }
//        }
//
//        // 根据部门名称匹配不上至少一个对应的部门
//        if (departments.size() == 0) {
//            for (ExcelDept fs : newExcelDepts) {
//                Department department = new Department();
//                department.setId(fs.getId());
//                department.setParentId(fs.getParentID());
//                department.setSapId(null);
//                department.setSapParentId(null);
//                department.setName(fs.getContent());
//                departments.add(department);
//            }
//        }
//        if (CollUtil.isNotEmpty(departments)) {
//            departmentMapper.insertBatch(departments);
//        }
        return "success";
    }

    @RequestMapping(value = "/initUser")
    @ResponseBody
    public String initUser() throws Exception {

        int number = userDao.count();
        if (number > 0) {
            return "No synchronization";
        }

        // 飞书用户列表查询
        List<ExcelUser> excelUsers = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("通讯录-导出.xlsx");
        EasyExcel.read(classPathResource.getInputStream(), ExcelUser.class, new AnalysisEventListener<ExcelUser>() {
            @Override
            public void invoke(ExcelUser user, AnalysisContext context) {
                excelUsers.add(user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        }).sheet().headRowNumber(3).doRead();

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
            user.setSapId(excelUser.getId());
            user.setName(excelUser.getName());
            user.setId(excelUser.getId());
            for (Department department : departments) {
                if (department.getName().equals(excelUser.getDeptName())) {
//                    user.setDeptId(department.getId());
                    break;
                }
            }
            users.add(user);
        }
        // 添加到用户映射表
        userDao.addList(users);

        return "success";
    }

    /**
     * SAP签名
     *
     * @param objects
     * @param secretKey
     * @param requestJson
     * @return
     */

}