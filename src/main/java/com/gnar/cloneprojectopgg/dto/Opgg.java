package com.gnar.cloneprojectopgg.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Opgg {
   
    @Getter
    @Setter
    public static class ResMain {
        private int status;                       //응답코드
        private String message;                   //성공, 실패
        private List<ResPart1> resPart1;          //성공, 실패
    }

    @Getter
    @Setter
    public static class ResPart1 {
        private List<SeasonTier> seasonTier;      //시즌별 티어 정보
        private String profileIconUrl;            //프로필 아이콘 이미지 URL
        private long summonerLevel;               //소환사 레벨
        private String name;                      //소환사 닉네임
        private String ladderRanking;             //래더 랭킹
    }

    @Getter
    @Setter
    public static class SeasonTier {
        private int season;
        private String tier;
    }

    

}
