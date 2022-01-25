package com.example.r6siegestats.r6sstatsapi.session;

import com.example.r6siegestats.r6sstatsapi.external.NoValidSessionException;
import com.example.r6siegestats.r6sstatsapi.external.UbiApiCommunicator;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiCreateSessionResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UbiApiSessionManager {
    private static final Logger log = LogManager.getLogger(UbiApiSessionManager.class);
    public static final String SESSION_FILE_NAME = "session_cache.json";

    private final UbiApiCommunicator communicator;

    private static List<SessionEntry> sessions = new ArrayList<>();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public UbiApiSessionManager(UbiApiCommunicator communicator) {
        this.communicator = communicator;
    }


    public boolean addSession(String username, String password) {
        for (SessionEntry session : sessions) {
            if (session.getUsername().equals(username)) {
                log.warn("Tried adding session which is already registered to the session manager - Username: '{}'", username);
                return false;
            }
        }

        // Session not registered yet - add it
        SessionEntry newSession = new SessionEntry();
        newSession.setUsername(username);
        newSession.setPassword(password);
        sessions.add(newSession);

        // Lets check if we have stored a ticket for this sessions
        Map<String, UbiCreateSessionResponse> loadedUbiSessions = loadStoredSessions();
        if (loadedUbiSessions != null) {
            UbiCreateSessionResponse loadedUbiSession = loadedUbiSessions.get(username);
            if (loadedUbiSession != null) {
                log.trace("Adding restored UbiSesssion to new registered session username={}", username);
                newSession.setUbiSession(loadedUbiSession);
            }

        }
        if (initSession(newSession)) {
            log.info("Successfully registered new session for user '{}' - UserId: '{}'", newSession.getUsername(), newSession.getUbiSession().userId);
            return true;
        }

        return false;
    }

    public boolean initSession(SessionEntry session) {
        var sessionValid = false;
        try {
            if (session.getUbiSession() != null && communicator.isSessionValid(session.getUbiSession())) {
                // Sessions was registered with stored ticket, let's try to reuse this...
                log.debug("Successfully reusing cached session with sessionId={}, username={}", session.getUbiSession().getSessionId(), session.getUsername());
                sessionValid = true;

            } else {
                log.warn("Unable to reuse-session, creating new login... - username=" + session.getUsername());
                session.setUbiSession(communicator.createSession(session.getUsername(), session.getPassword()));
                sessionValid = communicator.isSessionValid(session.getUbiSession());
            }
        } catch (UbiHardApiException | UbiApiErrorResponseException e) {
            log.warn("Unable to add session to session manager -  username={} - Message: {}", session.getUsername(), e.getMessage());
            return false;
        }

        session.setActive(sessionValid);

        storeSessions();
        return sessionValid;
    }

    public List<SessionEntry> getSessions() {
        return sessions;
    }

    private static Integer nextCounter = 0;

    /**
     * Get the next session (always +1)) from our available list of sessions.
     * If all sessions are invalid, it will throw an error.
     *
     * @return The selected session.
     * @throws NoValidSessionException When no valid session was found.
     */
    public SessionEntry getNextValidSession() throws NoValidSessionException {
        return getNextValidSession(0);
    }

    /**
     * This tries to get a valid session out of all session which we have available.
     * If tries is exhausted, it will fail with a NoValidSessionException
     * Session are selected on Round-Robin-Base.
     *
     * @param tryCount amount of tries a session has been tried to acquire
     * @return A valid session
     * @throws NoValidSessionException When not able to find a valid session after given amount of tries
     */
    protected SessionEntry getNextValidSession(final int tryCount) throws NoValidSessionException {
        if (tryCount > sessions.size()) {
            log.warn("No usable session remaining - Tried " + tryCount + " times.");
            throw new NoValidSessionException(tryCount);
        }
        if (sessions.size() == 0) {
            throw new IllegalStateException("No sessions set up - You need to setup sessions first");
        }

        if (nextCounter > sessions.size() - 1) {
            // When we reach the end of our session list, start from the beginning.
            nextCounter = 0;
        }

        SessionEntry selected = sessions.get(nextCounter++);
        if (!selected.isActive() || !selected.isUsable()) {
            // When the selected sessions is not usable, increase tryCount and try again.
            return getNextValidSession(tryCount + 1);
        }
        log.trace("Selected session: " + selected.getUsername());
        return selected;
    }

    /**
     * Stores the sessions persistent on the file-system for re-using them later.
     */
    public void storeSessions() {
        // Convert sessions to json to store them in a file.
        Map<String, UbiCreateSessionResponse> sessionsToStore = new HashMap<>();
        for (SessionEntry session : sessions) {
            if (session.isUsable()) {
                sessionsToStore.put(session.getUsername(), session.getUbiSession());
            }
        }
        // TODO Saving sessions on disk might not be the best idea... Store them in a cache maybe?
        try (Writer writer = new FileWriter(SESSION_FILE_NAME)) {
            gson.toJson(sessionsToStore, writer);
            log.debug("Wrote " + sessionsToStore.size() + " sessions to disk: " + SESSION_FILE_NAME);
        } catch (IOException e) {
            log.fatal("Unable to write sessions to disk: {}", e.getMessage(), e);
        }
    }

    /**
     * Loads the sessions from the file-system to avoid unnecessary re-auths after application restart.
     * Returns null when no sessions could be loaded
     */
    public Map<String, UbiCreateSessionResponse> loadStoredSessions() {

        File sessionsFile = new File(SESSION_FILE_NAME);
        if (!sessionsFile.exists()) {
            log.warn("Sessions file does not exists: {}", SESSION_FILE_NAME);
            return null;
        }

        try (Reader reader = new FileReader((sessionsFile))) {
            Map<String, UbiCreateSessionResponse> loadedUbiSessions = gson.fromJson(reader,
                    new TypeToken<Map<String, UbiCreateSessionResponse>>() {
                    }.getType());
            log.info("Loaded {} Ubi-Sessions from disk!", loadedUbiSessions.size());
            return loadedUbiSessions;
        } catch (IOException e) {
            log.fatal("Unable to read sessions from disk: {}", e.getMessage(), e);
        }
        return null;
    }

}
