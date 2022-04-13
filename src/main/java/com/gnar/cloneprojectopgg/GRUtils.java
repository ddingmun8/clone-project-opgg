package com.gnar.cloneprojectopgg;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.simple.JSONObject;

/**
 * GRUtils
 */
public class GRUtils {

    //URL Encoding
    public String urlEncode(String url) {
        String enUrl = "";
        try {
            enUrl = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enUrl;
    }
    

    //URL Ddcoding
    public String urlDecode(String url) {
        String deUrl = "";
        try {
            deUrl = URLDecoder.decode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deUrl;
    }

    public JSONObject jsonMerge(JSONObject jsonPart1, JSONObject jsonPart2) throws Exception {
        //최종으로 보낼 jsonObject
        JSONObject jsonMerge = new JSONObject();
        JSONObject[] objs = new JSONObject[] { jsonPart1, jsonPart2 };
        for (JSONObject obj : objs) {
            Iterator it = obj.keySet().iterator();
            while (it.hasNext()) {
                String key = (String)it.next();
                jsonMerge.put(key, obj.get(key));
            }
        }
        return jsonMerge;
    }

}