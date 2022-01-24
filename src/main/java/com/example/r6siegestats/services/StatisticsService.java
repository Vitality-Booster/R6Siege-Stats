package com.example.r6siegestats.services;

import com.example.r6siegestats.models.GetStatsRequest;
import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.external.UbiApiCommunicator;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiCreateSessionResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiPlayerResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiProfileResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.seasonal.summary.UbiSeasonalSummaryResponse;
import com.example.r6siegestats.services.interaces.IStatisticsService;
import com.example.r6siegestats.r6sstatsapi.session.UbiApiSessionManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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
    public CompletableFuture<UbiOverallQueueStats> getQueueStats(GetStatsRequest model) throws UbiHardApiException, UbiApiErrorResponseException, NoValidSessionException {
        var session = getSession(model);

        var response = communicator.getOverallQueueStats("21e4e8e4-b70a-4f8a-be4d-d0db7c8c9076", session);

        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<UbiSeasonalSummaryResponse> getSeasonalStats(GetStatsRequest model) throws UbiHardApiException, UbiApiErrorResponseException {
        var session = getSession(model);

        var response = communicator.getSeasonalSummary("21e4e8e4-b70a-4f8a-be4d-d0db7c8c9076", session);

        return CompletableFuture.completedFuture(response);
    }

    @Override
    public CompletableFuture<UbiPlayerResponse> getPlayerStats(GetStatsRequest model) throws UbiApiException {
        var session = getSession(model);

        var response = communicator.getPlayerOverview("21e4e8e4-b70a-4f8a-be4d-d0db7c8c9076", session);

        return CompletableFuture.completedFuture(response);
    }

    private UbiCreateSessionResponse getSession(GetStatsRequest model) {
        var sessions = sessionManager.loadStoredSessions();

        return sessions.get(model.getEmail());
    }

//    private String generateUuid() {
//        String letters = "abcdefghijklmnopqrstuvwxyz";
//        String digits = "0123456789";
//        String alphanumeric = letters + digits;
//        char[] charArray = alphanumeric.toCharArray();
//        StringBuilder userUuid = new StringBuilder();
//        SecureRandom random = new SecureRandom();
//        for (int i = 0; i < 32; i++) {
//            userUuid.append(charArray[random.nextInt(charArray.length)]);
//            if (i == 7 || i == 11 || i == 15 || i == 19) {
//                userUuid.append('-');
//            }
//        }
//        System.out.println(userUuid);
//        return userUuid.toString();
//    }
}
