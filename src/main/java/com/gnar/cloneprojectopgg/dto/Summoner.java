package com.gnar.cloneprojectopgg.dto;

import lombok.Getter;
import lombok.Setter;

public class Summoner {
    @Getter
    @Setter
    public static class Info {
        private String id;            //Encrypted summoner ID. Max length 63 characters.
        private String puuid;         //Encrypted summoner ID. Max length 63 characters.
    }
    @Getter
    @Setter
    public static class Match {
        private String matchId;       
    }

    @Getter
    @Setter
    public static class SummonerDTO {
        private String accountId;     //Encrypted account ID. Max length 56 characters.
        private int profileIconId;    //프로필 아이콘 이미지 ID
        private long revisionDate;    //최종 수정된 날짜
        private String name;          //소환사 닉네임
        private String id;            //Encrypted summoner ID. Max length 63 characters.
        private String puuid;         //해당 계정의 절대 불변하지 않는 고유 식별 ID
        private long summonerLevel;   //소환사 레벨
    }
/* 
    @Getter
    @Setter
    public static class Request {
        private String name;
        private int age;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String accountId;   //Encrypted account ID. Max length 56 characters.
        private String id;       //Encrypted summoner ID. Max length 63 characters.
    }
 */
}
