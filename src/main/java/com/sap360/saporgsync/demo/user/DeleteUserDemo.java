package com.sap360.saporgsync.demo.user;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.sap360.saporgsync.util.TimeUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

public class DeleteUserDemo {


    public static void makeMd5TokenAndUrl(String currentPage, String formID, String requestJson) {

        StringBuffer url = new StringBuffer();
        String timestamp = TimeUtil.getTimestamp();
        Map<String, String> objects = new HashMap<>();
        objects.put("APPID", "33461238");
        objects.put("COMPANYID", "100001");
        objects.put("FORMID", formID);
        objects.put("TIMESTAMP", timestamp);
        objects.put("DOCENTRY","229");
        String secretKey = "624728dd116f45648ae91715a9b5b306";
        String md5Token = makeMd5Token(objects, secretKey, requestJson);
        url.append("http://116.6.232.123:8059/OpenAPI/Company/Document/V1/Update/33461238/100001/")
                .append(formID).append("/").append("229").append("/").append(timestamp).append("/").append(md5Token);
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
        JSONObject requestJson = new JSONObject();
        requestJson.put("ComCode", "4");
        requestJson.put("lastName", "lastName02");
        requestJson.put("EnglishName1", "englishName02");
        requestJson.put("mobile", "12322321312");
        requestJson.put("email", "123@qq.com");
        requestJson.put("EmDept", "123");
        requestJson.put("RowStatus", "D");
        String requestJsonAddArg = "{\"U_OHEM\":[" + requestJson.toJSONString() + "],}";
        makeMd5TokenAndUrl("0", "-9644", requestJsonAddArg);
    }


}
