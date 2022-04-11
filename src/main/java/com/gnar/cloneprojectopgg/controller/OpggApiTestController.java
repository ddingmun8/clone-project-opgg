package com.gnar.cloneprojectopgg.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.dto.Opgg;
import com.gnar.cloneprojectopgg.dto.Summoner;
import com.google.gson.Gson;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@RestController
@RequestMapping(value = "/lol/test")
public class OpggApiTestController {
    GRUtils grUtils = new GRUtils();
    //최종 DTO
    Opgg.ResMain resMain = new Opgg.ResMain();
    //PART1 DTO
    Opgg.ResPart1 resPart1 = new Opgg.ResPart1();

    //최종 응답 JSONObject
    JSONObject jsonResMain = new JSONObject();
    JSONParser jParser = new JSONParser();
    Gson gson = new Gson();

    @Value("${RIOT_API_KEY}")
    private String riotApiKey;
    
    private String riotUrl = "https://kr.api.riotgames.com";
    private String riotUrl2 = "https://asia.api.riotgames.com";

    //소환사 정보검색(SUMMONER-V4)
    @GetMapping(path = "/findUserInfo/{searchName}")
    public JSONObject findUserInfo(@PathVariable(name = "searchName") String summonerName) {
        //소환사 명 인코딩
        summonerName = grUtils.URLEncode(summonerName);
        String requestUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

        JSONObject jsonUserInfo = new JSONObject();
        try {
            jsonUserInfo = getApiJson(requestUrl);

            
            //시즌별 티어
            JSONObject jsonST1 = new JSONObject();
            jsonST1.put("season", "2021");
            jsonST1.put("tier", "silver");
            JSONObject jsonST2 = new JSONObject();
            jsonST2.put("season", "2020");
            jsonST2.put("tier", "silver");

            JSONArray jsonArrST = new JSONArray();
            jsonArrST.add(jsonST1);
            jsonArrST.add(jsonST2);

            String profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/profileicon/" + jsonUserInfo.get("profileIconId") + ".png";

            jsonUserInfo.put("seasonTier", jsonArrST);
            jsonUserInfo.put("profileIconUrl", profileIconUrl);
            jsonUserInfo.put("ladderRanking", "1954803");

            //받아온 JSON 형식 데이터를 DTO에 담기
            ConvertDto(jsonUserInfo.toString());

            

            //DTO를 이용해서 JSON 데이터 jsonPart1
            JSONObject jsonPart1 = new JSONObject();
            System.out.println("resPart1 : " + resPart1);
            String strResPart1 = gson.toJson(resPart1);

            System.out.println("strResPart1 : " + strResPart1);

            Object obj = jParser.parse(strResPart1);
            jsonPart1 = (JSONObject) obj;

            //최종 전달 JSON 데이터
            jsonResMain.put("status", 200);
            jsonResMain.put("message", "SUCCESS");
            jsonResMain.put("resPart1", jsonPart1);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResMain;
    }
   
    //JSONObject를 DTO 클래스로 변환
    public void ConvertDto(String args) throws JsonProcessingException { 
        String jsonString = args;
        System.out.println("jsonString : " + jsonString);
        ObjectMapper objectMapper = new ObjectMapper(); 
        //선언한 필드만 매핑 
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        resPart1 = objectMapper.readValue(jsonString, Opgg.ResPart1.class);
        
        System.out.println(resPart1.getLadderRanking());
        System.out.println(resPart1.getName());
        System.out.println(resPart1.getProfileIconUrl());
        System.out.println(resPart1.getSummonerLevel());
        if(resPart1.getSeasonTier() != null){
            resPart1.getSeasonTier()
            .stream()
            .forEach(d -> {
                System.out.println(d.getSeason() + " : " + d.getTier());
            });
        }

    }

    public JSONObject getApiJson(String requestURL) throws Exception{
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

        if (response.getStatusLine().getStatusCode() == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);
            //System.out.println("body : " + body);

            jsonObj = (JSONObject) jParser.parse(body);
            //System.out.println("id : " + jsonObj.get("id").toString());
            System.out.println("jsonObj : " + jsonObj);
        }else{
            System.out.println("response is error : " + response.getStatusLine().getStatusCode());
        }

        return jsonObj;
    }

    public JSONArray getApiJsonArray(String requestURL) throws Exception{
        JSONArray jsonArray = new JSONArray();
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

        if (response.getStatusLine().getStatusCode() == 200) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);
            //System.out.println("body : " + body);

            jsonArray = (JSONArray) jParser.parse(body);
            //System.out.println("id : " + jsonObj.get("id").toString());
            System.out.println("jsonArray : " + jsonArray);
        }else{
            System.out.println("response is error : " + response.getStatusLine().getStatusCode());
        }

        return jsonArray;
    }

}
