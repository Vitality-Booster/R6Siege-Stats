package com.example.r6siegestats.services.interaces;

import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;

import java.util.concurrent.CompletableFuture;

public interface IStatisticsService {
    public CompletableFuture<UbiOverallQueueStats> getQueueStats(UserSignInRequest model) throws NoValidSessionException, UbiHardApiException, UbiApiErrorResponseException;
}
