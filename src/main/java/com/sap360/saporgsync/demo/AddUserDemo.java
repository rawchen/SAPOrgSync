package com.sap360.saporgsync.demo;

import cn.hutool.http.HttpRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AddUserDemo {


    public static void makeMd5TokenAndUrl(String queryId, String currentPage, String formID, String requestJson) {

        StringBuffer url = new StringBuffer();
        url.append("http://116.6.232.123:8059/");
        Map<String, String> objects = new HashMap<>();
        objects.put("APPID", "33461238");
        objects.put("COMPANYID", "100001");
        // 查询接口和写入接口区分
        if (StringUtils.isNotBlank(queryId)) {
            objects.put("QUERYID", queryId);
            objects.put("CURRENTPAGE", currentPage);
            url.append("OpenApi/Company/QueryData/V1/Query/33461238/100001/").append(queryId).append("/").append(currentPage).append("/");
        } else {
            objects.put("FORMID", formID);
            url.append("OpenAPI/Company/Document/V1/Add/33461238/100001/").append(formID).append("/");
            // objects.put("DOCENTRY","24");
            // url.append("OpenAPI/Company/Document/V1/Update/33461238/49305/").append(formID).append("/").append("24").append("/");

        }
        objects.put("TIMESTAMP", (Calendar.getInstance().getTimeInMillis() / 1000) + "");
        //  objects.put("TIMESTAMP","1661502941");


        url.append(objects.get("TIMESTAMP")).append("/");
        String secretKey = "624728dd116f45648ae91715a9b5b306";
        String md5Token = makeMd5Token(objects, secretKey, requestJson);
        url.append(md5Token);
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
        makeMd5TokenAndUrl(null, "0", "-9644", "{\n" +
                "    \"U_OHEM\": [\n" +
                "        {\n" +
                "            \"ComCode\": \"4\",\n" +
                "            \"EmDept\": \"123\",\n" +
                "            \"lastName\": \"1\",\n" +
                "            \"EnglishName\": \"1\",\n" +
                "            \"jobTitle\": \"1\",\n" +
                "            \"TimeOfEntry\": \"2013-12-18T00:00:00+08:00\",\n" +
                "            \"status\": \"1\",\n" +
                "            \"mobile\": \"18259234116\",\n" +
                "            \"email\": \"123@qq.com\",\n" +
                "            \"sex\": \"M\",\n" +
                "            \"officeTel\": \"111111\",\n" +
                "            \"RowStatus\": \"A\",\n" +
                "            \"DocEntry\": \"219\"\n" +
                "        }\n" +
                "    ]\n" +
                "}");

    }


}
