package com.example.r6siegestats.r6sstatsapi.external.objects.respones;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class UbiPlayerResponse {

    @SerializedName("players")
    private Map<String, Player> players;

    @Data
    public class Player {
        @SerializedName("max_mmr")
        private Integer maxMmr;

        @SerializedName("skill_mean")
        private Float skillMean;

        @SerializedName("deaths")
        private Integer deaths;

        @SerializedName("profile_id")
        private String profileId;

        @SerializedName("next_rank_mmr")
        private Integer nextRankMmr;

        @SerializedName("rank")
        private Integer rank;

        @SerializedName("max_rank")
        private Integer maxRank;

        @SerializedName("board_id")
        private String boardId;

        @SerializedName("skill_stdev")
        private Float skillStdev;

        @SerializedName("kills")
        private Integer kills;

        @SerializedName("last_match_skill_stdev_change")
        private Float lastMatchSkillStdevChange;

        @SerializedName("update_time")
        private Date updateTime;

        @SerializedName("last_match_mmr_change")
        private Integer lastMatchMmrChange;

        @SerializedName("abandons")
        private Integer abandons;

        @SerializedName("season")
        private Integer season;

        @SerializedName("top_rank_position")
        private Integer topRankPosition;

        @SerializedName("last_match_skill_mean_change")
        private Float lastMatchSkillMeanChange;

        @SerializedName("mmr")
        private Integer mmr;

        @SerializedName("previous_rank_mmr")
        private Integer previousRankMmr;

        @SerializedName("last_match_result")
        private Integer lastMatchResult;

        @SerializedName("wins")
        private Integer wins;

        @SerializedName("region")
        private String region;

        @SerializedName("losses")
        private Integer losses;


    }
}
