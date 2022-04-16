package com.gnar.cloneprojectopgg;

import java.util.Iterator;

import com.google.gson.Gson;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * OGUtils - OPGG API 사용에 필요한 유틸모음
 */
public class OGUtils {
    JSONParser jParser = new JSONParser();
    Gson gson = new Gson();

    @SuppressWarnings("unchecked")
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

    //OPGG getApiJson
    @SuppressWarnings("unchecked")
    public JSONObject getApiJson(String requestURL, String riotApiKey) throws Exception{
        JSONObject jsonObj = new JSONObject();
        JSONParser jParser = new JSONParser();

        //get 메서드와 URL 설정
        HttpGet httpGet = new HttpGet(requestURL);

        //header 설정
        httpGet.addHeader("User-Agent", "Mozilla/5.0");
        httpGet.addHeader("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        httpGet.addHeader("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.addHeader("Origin", "https://developer.riotgames.com");
        httpGet.addHeader("X-Riot-Token", riotApiKey);

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(httpGet);

        int stateCode = response.getStatusLine().getStatusCode();

        if (stateCode == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);

            jsonObj = (JSONObject) jParser.parse(body);
            jsonObj.put("status", stateCode);
        }else{
            jsonObj.put("status", stateCode);

            System.out.println("response is error : " + response.getStatusLine().getStatusCode());
        }

        return jsonObj;
    }

}