package com.gnar.cloneprojectopgg.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.api.ResPart1Api;
import com.gnar.cloneprojectopgg.dto.Opgg;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@RestController
@RequestMapping(value = "/lol/temp")
public class ResPart1Controller {
    GRUtils grUtils = new GRUtils();
    ResPart1Api resPart1Api = new ResPart1Api();
    JSONParser jParser = new JSONParser();
    Gson gson = new Gson();

    @Value("${RIOT_API_KEY}")
    private String riotApiKey;
    
    //최종 DTO
    Opgg.ResMain dtoResMain = new Opgg.ResMain();
    //최종 resPart1DTO
    Opgg.ResPart1 dtoResPart1 = new Opgg.ResPart1();

    //최종 응답 JSONObject
    JSONObject jsonResMain = new JSONObject();

    //소환사 정보검색(SUMMONER-V4)
    @GetMapping(path = "/findUserInfo/{summonerName}")
    public JSONObject findUserInfo(@PathVariable(name = "summonerName") String summonerName) {
        try {
            //resPart1  JSON 만들기
            //1. 라이엇 API 사용자 이름, 레벨, 아이콘
            JSONObject jsonSummonerInfo = resPart1Api.apiSummonerInfo(summonerName,riotApiKey);
            //2. 시즌별 티어, 프로필 아이콘 URL jsonObject에 담기
            JSONArray jsonArrST = getSeasonTier();
            String profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/profileicon/" + jsonSummonerInfo.get("profileIconId") + ".png";
            
            JSONObject jsonInfo = new JSONObject();
            jsonInfo.put("seasonTier", jsonArrST);
            jsonInfo.put("profileIconUrl", profileIconUrl);
            jsonInfo.put("ladderRanking", "1954803");

            //resMergePart1 = jsonSummonerInfo + jsonInfo (모든정보 합치기)
            JSONObject resMergePart1 = grUtils.JSONMerge(jsonSummonerInfo, jsonInfo);

            //dtoResPart1를 이용하여 필요한 정보만 담기
            ConvertDto(resMergePart1.toString());

            //필요한 정보만 담긴 DTO를 이용해서 최종 JSONObject resPart1 생성
            String strResPart1 = gson.toJson(dtoResPart1);
            Object objResPart1 = jParser.parse(strResPart1);
            JSONObject resPart1 = (JSONObject) objResPart1;

            //최종 jsonResMain 데이터
            jsonResMain.put("resPart1", resPart1);

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
        dtoResPart1 = objectMapper.readValue(strJsonObject, Opgg.ResPart1.class);
        
        if(dtoResPart1.getSeasonTier() != null){
            dtoResPart1.getSeasonTier()
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

}