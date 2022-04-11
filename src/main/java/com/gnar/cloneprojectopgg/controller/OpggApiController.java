package com.gnar.cloneprojectopgg.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnar.cloneprojectopgg.GRUtils;
import com.gnar.cloneprojectopgg.dto.Summoner;

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
@RequestMapping(value = "/lol")
public class OpggApiController {
    GRUtils grUtils = new GRUtils();
    Summoner.ReqSummonerInfo reqSummonerInfo = new Summoner.ReqSummonerInfo();
    Summoner.ResSummonerInfo resSummonerInfo = new Summoner.ResSummonerInfo();
    Summoner.Match summonerMatch = new Summoner.Match();

    @Value("${RIOT_API_KEY}")
    private String riotApiKey;
    
    private String riotUrl = "https://kr.api.riotgames.com";
    private String riotUrl2 = "https://asia.api.riotgames.com";

    //최종 응답 JSONObject
    JSONObject resJsonObj = new JSONObject();
    JSONParser jParser = new JSONParser();

    //소환사 정보검색(SUMMONER-V4)
    @GetMapping(path = "/findUserInfo/{searchName}")
    public JSONObject findUserInfo(@PathVariable(name = "searchName") String summonerName) {
        //소환사 명 인코딩
        summonerName = grUtils.URLEncode(summonerName);
        String requestUrl = riotUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

        
        JSONObject jsonObject = new JSONObject();

        JSONObject jsonUserInfo = new JSONObject();
        try {
            jsonUserInfo = getApiJson(requestUrl);
            reqSummonerInfo.setId(jsonUserInfo.get("id").toString());
            reqSummonerInfo.setPuuid(jsonUserInfo.get("puuid").toString());
            reqSummonerInfo.setProfileIconId(jsonUserInfo.get("profileIconId").toString());

            String profileIconUrl = "https://ddragon.leagueoflegends.com/cdn/12.6.1/img/profileicon/" + reqSummonerInfo.getProfileIconId() + ".png";
           /* 
            resSummonerDTO.setSeasonTier("");
            resSummonerDTO.setProfileIconUrl(profileIconUrl);
            resSummonerDTO.setSummonerLevel((long)jsonUserInfo.get("summonerLevel"));
            resSummonerDTO.setName(jsonUserInfo.get("name").toString());
            resSummonerDTO.setLadderRanking("");

            ObjectMapper mapper = new ObjectMapper(); 
            String jsonString = mapper.writeValueAsString(resSummonerDTO);
            System.out.println(jsonString);

            jsonObj = (JSONObject) jParser.parse(jsonString);
             */

            //
            
            resJsonObj.put("status", 200);
            resJsonObj.put("message", "SUCCES");
            
            JSONObject resSummonerInfo = new JSONObject();
            resSummonerInfo.put("seasonTier", "");
            resSummonerInfo.put("profileIconUrl", profileIconUrl);
            resSummonerInfo.put("summonerLevel", (long)jsonUserInfo.get("summonerLevel"));
            resSummonerInfo.put("name", jsonUserInfo.get("name").toString());
            resSummonerInfo.put("ladderRanking", "");

            JSONArray req_array = new JSONArray();
            req_array.add(resSummonerInfo);
            
            resJsonObj.put("resSummonerInfo", resSummonerInfo);
            resJsonObj.put("resSummonerInfo2", req_array);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resJsonObj;
    }
   
    //소환사 LEAGUE 정보(LEAGUE-V4)
    @RequestMapping(value = "/findUserLeague")
    public JSONArray findUserLeague() {
        String requestUrl = riotUrl + "/lol/league/v4/entries/by-summoner/" + reqSummonerInfo.getId();
        JSONArray jsonUserLeague = new JSONArray();
        try {
            jsonUserLeague = getApiJsonArray(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonUserLeague;
    } 

    //계정정보(PUUID)로 게임 MATCH ID 받기(MATCH-V5)
    @RequestMapping(value = "/findMatchId")
    public JSONArray findMatchId() {
        String requestUrl = riotUrl2 + "/lol/match/v5/matches/by-puuid/" + reqSummonerInfo.getPuuid() + "/ids";
        JSONArray jsonUMatchList = new JSONArray();
        try {
            jsonUMatchList = getApiJsonArray(requestUrl);
            summonerMatch.setMatchId(jsonUMatchList.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonUMatchList;
    } 

    //MATCH ID로 해당 게임 관련 정보 받기(MATCH-V5)
    @RequestMapping(value = "/findMatchInfo")
    public JSONObject findMatchInfo() {
        String requestUrl = riotUrl2 + "/lol/match/v5/matches/" + summonerMatch.getMatchId();
        JSONObject jsonMatchInfo = new JSONObject();
        try {
            jsonMatchInfo = getApiJson(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonMatchInfo;
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

    
    //TEST
    @RequestMapping(value = "/callbackTest1")
    public ModelAndView test1(HttpServletRequest request, ModelMap model) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
        JSONObject jsonRes = new JSONObject();


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

        //주황
        JSONObject jsonPart1 = new JSONObject();
        jsonPart1.put("name", "초코잠보");
        jsonPart1.put("url", "test");
        jsonPart1.put("seasonTier", "jsonArrST");

        //주황
        JSONObject jsonPart2 = new JSONObject();
        jsonPart2.put("test", "test");
        jsonPart2.put("test", "test2");
        jsonPart2.put("test", "test3");

        mav.addObject("status", "200");
        mav.addObject("message", "SUCCESS");
        mav.addObject("seasonTier", jsonArrST);
        mav.addObject("url", "test");
        mav.addObject("name", "초코잠보");
        mav.addObject("jsonPart1", jsonPart1);
        mav.addObject("jsonPart2", jsonPart2);

        return mav;

    }

    //TEST
    @RequestMapping(value = "/callbackTest2")
    public JSONObject test2(HttpServletRequest request, ModelMap model) throws Exception {

        JSONObject jsonRes = new JSONObject();


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

        //주황
        JSONObject jsonPart1 = new JSONObject();
        jsonPart1.put("name", "초코잠보");
        jsonPart1.put("url", "test");
        jsonPart1.put("seasonTier", "jsonArrST");

        //주황
        JSONObject jsonPart2 = new JSONObject();
        jsonPart2.put("test", "test");
        jsonPart2.put("test", "test2");
        jsonPart2.put("test", "test3");

        jsonRes.put("status", "200");
        jsonRes.put("message", "SUCCESS");
        jsonRes.put("seasonTier", jsonArrST);
        jsonRes.put("url", "test");
        jsonRes.put("name", "초코잠보");
        jsonRes.put("jsonPart1", jsonPart1);
        jsonRes.put("jsonPart2", jsonPart2);

        return jsonRes;

    }

}
