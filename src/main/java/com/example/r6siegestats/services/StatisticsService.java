package com.example.r6siegestats.services;

import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.external.UbiApiCommunicator;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;
import com.example.r6siegestats.services.interaces.IStatisticsService;
import com.example.r6siegestats.r6sstatsapi.session.UbiApiSessionManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class StatisticsService implements IStatisticsService {
    private final UbiApiCommunicator communicator;
    private final UbiApiSessionManager sessionManager;

    public StatisticsService(UbiApiCommunicator communicator, UbiApiSessionManager sessionManager) {
        this.communicator = communicator;
        this.sessionManager = sessionManager;
    }


    @Override
    public CompletableFuture<UbiOverallQueueStats> getQueueStats(UserSignInRequest model) throws UbiHardApiException, UbiApiErrorResponseException, NoValidSessionException {
        sessionManager.addSession(model.getEmail(), model.getPassword());
        var response = communicator.getOverallQueueStats("21e4e8e4-b70a-4f8a-be4d-d0db7c8c9076", sessionManager.getNextValidSession().getUbiSession());

        return CompletableFuture.completedFuture(response);
    }
}
