package com.gnar.cloneprojectopgg.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.OGUtils;
import com.gnar.cloneprojectopgg.api.ResPart1Api;
import com.gnar.cloneprojectopgg.dto.Opgg;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@RestController
@RequestMapping(value = "/lol")
public class OpggController {
    GRUtils grUtils = new GRUtils();
    OGUtils ogUtils = new OGUtils();
    
    ResPart1Api resPart1Api = new ResPart1Api();
    JSONParser jParser = new JSONParser();
    Gson gson = new Gson();

    @Value("${RIOT_API_KEY}")
    private String riotApiKey;
    
    //최종 DTO
    Opgg.ResMain dtoResMain = new Opgg.ResMain();

    //최종 응답 JSONObject
    JSONObject jsonResMain = new JSONObject();

    String strStaCode = "";

    //소환사 정보검색(SUMMONER-V4)
    @GetMapping(path = "/findUserInfo/{summonerName}")
    public JSONObject findUserInfo(@PathVariable(name = "summonerName") String summonerName) {
        try {
            //part1 JOSNObject
            JSONObject resMergePart1 = resPart1Api.getPart1Info(summonerName, riotApiKey);
            String strStatus = resMergePart1.get("status").toString();
            
            //jsonResMain에 status, message 추가
            strStaCode = addStaMsg(strStatus);
            
            if(strStaCode == "SUCCESS"){
                //dtoResPart1를 이용하여 필요한 정보만 담기resPart1
                JSONObject resPart1 = resPart1Api.convertDto(resMergePart1.toString());
                jsonResMain.put("resPart1", resPart1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonResMain;
    }

    public String addStaMsg(String state){
        int stateCode = Integer.parseInt(state);
        String status = "";

        if(stateCode != 200){
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "FALSE");
            status = "FALSE";
        }else{
            jsonResMain.put("status", stateCode);
            jsonResMain.put("message", "SUCCESS");
            status = "SUCCESS";
        }

        return status;
    }

}