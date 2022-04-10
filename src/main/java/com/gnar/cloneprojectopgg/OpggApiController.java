package com.gnar.cloneprojectopgg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    Summoner.Info summonerInfo = new Summoner.Info();
    Summoner.Match summonerMatch = new Summoner.Match();

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
            summonerInfo.setId(jsonUserInfo.get("id").toString());
            summonerInfo.setPuuid(jsonUserInfo.get("puuid").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonUserInfo;
    }

    //소환사 LEAGUE 정보(LEAGUE-V4)
    @RequestMapping(value = "/findUserLeague")
    public JSONArray findUserLeague() {
        String requestUrl = riotUrl + "/lol/league/v4/entries/by-summoner/" + summonerInfo.getId();
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
        String requestUrl = riotUrl2 + "/lol/match/v5/matches/by-puuid/" + summonerInfo.getPuuid() + "/ids";
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

}
