package com.gnar.cloneprojectopgg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class Summoner {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Info {
        private String accountId;   //Encrypted account ID. Max length 56 characters.
        private int profileIconId;  //ID of the summoner icon associated with the summoner.
        private long revisionDate;  //Date summoner was last modified specified as epoch milliseconds. The following events will update this timestamp: summoner name change, summoner level change, or profile icon change.
        private String name;        //Summoner name.
        private String id;       //Encrypted summoner ID. Max length 63 characters.
        private String puuid;       //Encrypted PUUID. Exact length of 78 characters.
        private long summonerLevel; //Summoner level associated with the summoner.
    }

/*     @Getter
    @Setter
    public static class Request {
        private String name;
        private int age;
    } */

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String accountId;   //Encrypted account ID. Max length 56 characters.
        private String id;       //Encrypted summoner ID. Max length 63 characters.
    }
}
