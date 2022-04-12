package com.gnar.cloneprojectopgg.api;

import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.OGUtils;
import org.json.simple.JSONObject;

public class ResPart1Api {
    GRUtils grUtils = new GRUtils();
    OGUtils ogUtils = new OGUtils();

    private String riotUrl = "https://kr.api.riotgames.com";

    //소환사 정보검색(SUMMONER-V4)
    @SuppressWarnings("unchecked")
    public JSONObject apiSummonerInfo(String summonerName, String riotApiKey) throws Exception {

        summonerName = grUtils.URLEncode(summonerName);
        String requestUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

        return ogUtils.getApiJson(requestUrl, riotApiKey);
    }
}