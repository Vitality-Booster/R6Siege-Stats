package com.example.r6siegestats.services.interaces;

import com.example.r6siegestats.models.GetStatsRequest;
import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiPlayerResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiProfileResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.seasonal.summary.UbiSeasonalSummaryResponse;

import java.util.concurrent.CompletableFuture;

public interface IStatisticsService {
    CompletableFuture<UbiOverallQueueStats> getQueueStats(GetStatsRequest model) throws NoValidSessionException, UbiHardApiException, UbiApiErrorResponseException;

    CompletableFuture<UbiSeasonalSummaryResponse> getSeasonalStats(GetStatsRequest model) throws UbiHardApiException, UbiApiErrorResponseException;

    CompletableFuture<UbiPlayerResponse> getPlayerStats(GetStatsRequest model) throws UbiApiException;
}
