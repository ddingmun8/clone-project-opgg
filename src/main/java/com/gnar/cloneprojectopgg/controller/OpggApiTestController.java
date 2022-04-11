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
        
        
        try {
            //resPart1  JSON 만들기
            //1. 라이엇 API 사용자 이름, 레벨, 아이콘
            JSONObject jsonDto = getApiJson(requestUrl);
            //2. 시즌별 티어
            JSONArray jsonArrST = getSeasonTier();
            //3. 프로필 아이콘 URL
            String profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/profileicon/" + jsonDto.get("profileIconId") + ".png";

            //1~3 데이터 JSON형식으로 합치기
            jsonDto.put("seasonTier", jsonArrST);
            jsonDto.put("profileIconUrl", profileIconUrl);
            jsonDto.put("ladderRanking", "1954803");

            //resPart1 모든 정보 DTO에 담기
            ConvertDto(jsonDto.toString());

            //DTO를 이용해서 최종 JSONObject resPart1 생성
            String strResPart1 = gson.toJson(resPart1);
            Object objResPart1 = jParser.parse(strResPart1);
            JSONObject jsonPart1 = (JSONObject) objResPart1;

            //최종 jsonResMain 데이터
            jsonResMain.put("resPart1", jsonPart1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResMain;
    }
   
    //JSONObject를 DTO 클래스로 변환
    public void ConvertDto(String strJsonObject) throws JsonProcessingException { 
        ObjectMapper objectMapper = new ObjectMapper(); 
        //선언한 필드만 매핑 
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        resPart1 = objectMapper.readValue(strJsonObject, Opgg.ResPart1.class);
        
        if(resPart1.getSeasonTier() != null){
            resPart1.getSeasonTier()
            .stream()
            .forEach(d -> {
            });
        }

    }

    //시즌별 티어 임시
    public JSONArray getSeasonTier() throws Exception{
        //시즌별 티어
        JSONArray jsonArrST = new JSONArray();

        String[] season = {"2018", "2019", "2020", "2021"};
        String[] tier = {"Bronze", "Silver", "Gold", "Platinum"};
        for(int i=0; i<4; i++){
            JSONObject jsonST = new JSONObject();
            jsonST.put("season", season[i]);
            jsonST.put("tier", tier[i]);

            jsonArrST.add(jsonST);
        }
        
        return jsonArrST;
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

        int stateCode = response.getStatusLine().getStatusCode();

        if (stateCode == 200) {
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "SUCCESS");

            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);

            jsonObj = (JSONObject) jParser.parse(body);
        }else{
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "FAILS");

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

        int stateCode = response.getStatusLine().getStatusCode();

        if (stateCode == 200) {
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "SUCCESS");

            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);

            jsonArray = (JSONArray) jParser.parse(body);
        }else{
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "FAILS");

            System.out.println("response is error : " + response.getStatusLine().getStatusCode());
        }

        return jsonArray;
    }
}
