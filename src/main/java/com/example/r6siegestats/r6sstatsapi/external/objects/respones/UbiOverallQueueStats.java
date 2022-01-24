package com.example.r6siegestats.r6sstatsapi.external.objects.respones;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.HashMap;

@Data
public class UbiOverallQueueStats {
    @SerializedName("results")
    private HashMap<String, ResultEntry> results;

    @Data
    public class ResultEntry {
        @SerializedName("rankedpvp_timeplayed:infinite")
        private Long rankedPvpTimePlayed;

        @SerializedName("rankedpvp_matchlost:infinite")
        private Integer rankedPvpMatchesLost;

        @SerializedName("rankedpvp_matchwon:infinite")
        private Integer rankedPvpMatchesWon;

        @SerializedName("rankedpvp_kills:infinite")
        private Integer rankedPvpKills;

        @SerializedName("rankedpvp_death:infinite")
        private Integer rankedPvpDeaths;

        @SerializedName("rankedpvp_matchplayed:infinite")
        private Integer rankedPvpMatchesPlayer;
    }
}
