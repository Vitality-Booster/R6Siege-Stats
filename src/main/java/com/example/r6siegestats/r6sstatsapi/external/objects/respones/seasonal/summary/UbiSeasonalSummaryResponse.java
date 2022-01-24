package com.example.r6siegestats.r6sstatsapi.external.objects.respones.seasonal.summary;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.Date;
import java.util.Map;

@Data
public class UbiSeasonalSummaryResponse {
    private String profileId;
    private String region;
    private String statType;

    @SerializedName("platforms")
    private Map<String, UbiPlatform> platforms;

    @SerializedName("executionTime")
    private Integer executionTime;

    @SerializedName("returned_time")
    private Date returnedTime;
}
