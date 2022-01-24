package com.example.r6siegestats.r6sstatsapi.external;

import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.session.UbiApiSessionManager;
import com.example.r6siegestats.r6sstatsapi.external.UbiApiCommunicator;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiPlayerResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiProfileResponse;
import com.example.r6siegestats.r6sstatsapi.session.SessionEntry;
import com.example.r6siegestats.r6sstatsapi.session.UbiApiSessionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class UbiApiRequestExecutor {
    public static final Logger log = LogManager.getLogger(UbiApiRequestExecutor.class);

    public static final int EXECUTOR_THREADS = 3;

    private final ExecutorService executorService;
    private UbiApiCommunicator communicator;
    private final UbiApiSessionManager sessionManager;

    public UbiApiRequestExecutor(UbiApiCommunicator communicator, UbiApiSessionManager ubiApiSessionManager) {
        this.communicator = communicator;
        this.sessionManager = ubiApiSessionManager;

        this.executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS,
                new ThreadFactory() {
                    private int counter = 0;

                    @Override
                    public Thread newThread(Runnable runnable) {
                        final int num = counter++;
                        log.debug("UbiApiRequestExecutor: " + num);
                        return new Thread(runnable, "UbiApiRequestExecutor-" + num);
                    }
                });
        log.trace(UbiApiRequestExecutor.class + " ExecutorService started!");
    }

    /**
     * Profile
     */
    public class RequestProfileCallable implements Callable<UbiProfileResponse> {

        public RequestProfileCallable() {
        }

        @Override
        public UbiProfileResponse call() throws UbiApiException, NoValidSessionException {
            SessionEntry session = sessionManager.getNextValidSession();
            return communicator.getProfile(session.getUbiSession().getProfileId(), session.getUbiSession());
        }
    }

    public Future<UbiProfileResponse> getProfile() {
        return executorService.submit(new RequestProfileCallable());
    }


    /**
     * Player
     */
    public class RequestPlayerCallable implements Callable<UbiPlayerResponse> {

        private final String uuid;

        public RequestPlayerCallable(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public UbiPlayerResponse call() throws UbiApiException, NoValidSessionException {
            SessionEntry session = sessionManager.getNextValidSession();
            return communicator.getPlayerOverview(this.uuid, session.getUbiSession());
        }
    }

    public Future<UbiPlayerResponse> getPlayer(String uuid) {
        return executorService.submit(new RequestPlayerCallable(uuid));
    }


    /**
     * Rank Queue
     */
    public class RequestOverallQueueCallable implements Callable<UbiOverallQueueStats> {

        private final String uuid;

        public RequestOverallQueueCallable(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public UbiOverallQueueStats call() throws UbiApiException, NoValidSessionException {
            SessionEntry session = sessionManager.getNextValidSession();
            return communicator.getOverallQueueStats(this.uuid, session.getUbiSession());
        }
    }

    public Future<UbiOverallQueueStats> getOverallRankQueue(String uuid) {
        return executorService.submit(new RequestOverallQueueCallable(uuid));
    }



    public void getTest() throws NoValidSessionException, UbiHardApiException, UbiApiErrorResponseException {
        SessionEntry session = sessionManager.getNextValidSession();
        communicator.getTest(session.getUbiSession());

    }

}
