package com.gnar.cloneprojectopgg.api;

import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.OGUtils;
import com.gnar.cloneprojectopgg.dto.Opgg;
import com.google.gson.Gson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ResPart1Api {
    GRUtils grUtils = new GRUtils();
    OGUtils ogUtils = new OGUtils();
    JSONParser jParser = new JSONParser();
    Gson gson = new Gson();

    Opgg.ResPart1 dtoResPart1 = new Opgg.ResPart1();
    private String riotUrl = "https://kr.api.riotgames.com";

    //ResPart1 JSONObject
    public JSONObject getPart1Info(String summonerName, String riotApiKey){
        //최종 리턴할 resPart1
        JSONObject resMergePart1 = new JSONObject();
        try {
            //resPart1  JSON 만들기
            //1. 라이엇 API 사용자 이름, 레벨, 아이콘
            JSONObject jsonSummonerInfo = apiSummonerInfo(summonerName, riotApiKey);
            //2. 시즌별 티어, 프로필 아이콘 URL jsonObject에 담기
            JSONArray jsonArrST = getSeasonTier();
            String profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/profileicon/" + jsonSummonerInfo.get("profileIconId") + ".png";
            
            HashMap<String,Object> hashInfo = new HashMap<String,Object>();
            hashInfo.put("seasonTier", jsonArrST);
            hashInfo.put("profileIconUrl", profileIconUrl);
            hashInfo.put("ladderRanking", "1954803");

            JSONObject jsonInfo = new JSONObject(hashInfo);

            //resMergePart1 = jsonSummonerInfo + jsonInfo (모든정보 합치기)
            resMergePart1 = ogUtils.jsonMerge(jsonSummonerInfo, jsonInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resMergePart1;
    }

    //소환사 정보검색(SUMMONER-V4)
    public JSONObject apiSummonerInfo(String summonerName, String riotApiKey) throws Exception {

        summonerName = grUtils.urlEncode(summonerName);
        String requestUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

        return ogUtils.getApiJson(requestUrl, riotApiKey);
    }

    //시즌별 티어 임시
    @SuppressWarnings("unchecked")
    public JSONArray getSeasonTier() throws Exception{
        //시즌별 티어
        JSONArray jsonArrST = new JSONArray();

        String[] season = {"2018", "2019", "2020", "2021"};
        String[] tier = {"Bronze", "Silver", "Gold", "Platinum"};
        for(int i=0; i<4; i++){

            HashMap<String,Object> hashST = new HashMap<String,Object>();
            hashST.put("season", season[i]);
            hashST.put("tier", tier[i]);

            JSONObject jsonST = new JSONObject(hashST);

            jsonArrST.add(jsonST);
        }
        
        return jsonArrST;
    }
    
    public JSONObject convertDto(String strJsonObject) throws JsonProcessingException { 
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

        //최종 JSONObject resPart1 생성
        String strResPart1 = gson.toJson(dtoResPart1);
        Object objResPart1 = new Object();
        try {
            objResPart1 = jParser.parse(strResPart1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return (JSONObject) objResPart1;
    }
}