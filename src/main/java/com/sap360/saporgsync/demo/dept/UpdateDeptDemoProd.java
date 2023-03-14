package com.sap360.saporgsync.demo.dept;

import cn.hutool.http.HttpRequest;
import com.sap360.saporgsync.util.TimeUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class UpdateDeptDemoProd {


    public static void makeMd5TokenAndUrl(String currentPage, String formID, String requestJson) {

        StringBuffer url = new StringBuffer();
        String timestamp = TimeUtil.getTimestamp();
        Map<String, String> objects = new HashMap<>();
        objects.put("APPID", "33461238");
        objects.put("COMPANYID", "100000");
        objects.put("FORMID", formID);
        objects.put("TIMESTAMP", timestamp);
        objects.put("DOCENTRY","51");
        String secretKey = "624728dd116f45648ae91715a9b5b306";
        String md5Token = makeMd5Token(objects, secretKey, requestJson);
        url.append("http://116.6.232.124:8059/OpenAPI/Company/Document/V1/Update/33461238/100000/")
                .append(formID).append("/").append("51").append("/").append(timestamp).append("/").append(md5Token);
        System.out.println("url:" + url);
        try {
            String s = HttpRequest.post(url.toString())
                    .body(requestJson)
                    .execute()
                    .body();
            System.out.println("r:" + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String makeMd5Token(Map<String, String> objects, String secretKey, String requestJson) {
        StringBuffer content = new StringBuffer();
        content.append(secretKey);
        // 对resultmap中的参数进行排序
        List<String> keyList = new ArrayList<String>();
        Iterator<Map.Entry<String, String>> ite = objects.entrySet().iterator();
        while (ite.hasNext()) {
            keyList.add(ite.next().getKey());
        }
        Collections.sort(keyList);
        // 拼接secretKey
        for (String key : keyList) {
            content.append(key).append(objects.get(key));
        }
        content.append(requestJson).append(secretKey);
        System.out.println(content.toString());
        // 生成md5签名
        return DigestUtils.md5Hex(content.toString());
    }


    public static void main(String[] args) {
        makeMd5TokenAndUrl("0", "-96134", "{\n" +
                "    \"U_OEPT\":[\n" +
                "        {\n" +
                "            \"DocEntry\":51,\n" +
                "            \"RowStatus\":\"U\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"U_EPT1\":[\n" +
                "        {\n" +
                "            \"ParentName\":\"物流&质检部\",\n" +
                "            \"DeptName\":\"香港物流分部\",\n" +
                "            \"LineNum\":1,\n" +
                "            \"RowStatus\":\"U\"\n" +
                "        },\n" +
                "    ]\n" +
                "}");
    }


}
