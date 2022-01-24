package com.example.r6siegestats.r6sstatsapi.external;

import com.google.gson.Gson;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiErrorResponseException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.error.UbiHardApiException;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiCreateSessionResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiOverallQueueStats;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiPlayerResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiProfileResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.respones.seasonal.summary.UbiSeasonalSummaryResponse;
import com.example.r6siegestats.r6sstatsapi.external.objects.specific.UbiPlatform;
import com.example.r6siegestats.r6sstatsapi.external.objects.specific.UbiSpace;
import com.example.r6siegestats.r6sstatsapi.external.objects.specific.UbiWebserviceConsts;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service
public class UbiApiCommunicator {
    private static final Logger log = LogManager.getLogger(UbiApiCommunicator.class);

    protected static final MediaType MEDIA_TYPE_JSON_UTF8 = MediaType.get("application/json; charset=utf-8");

    private final Gson gson;

    private OkHttpClient client = new OkHttpClient();

    public UbiApiCommunicator(@NotNull Gson gson) {
        this.gson = gson;
    }


    /**
     * Prepare an authorized request.
     *
     * @param url
     * @param session
     * @return
     */
    private Request.Builder authorizedRequestBuilder(String url, UbiCreateSessionResponse session) {
        log.debug("Request URL: " + url);
        return new Request.Builder()
                .url(url)
                .header("Ubi-AppId", UbiWebserviceConsts.UBI_APPID_VALUE)
                .header("Authorization", "ubi_v1 t=" + session.getTicket())
                .header("Ubi-SessionId", session.getSessionId());
    }

    private <T> T authorizedGetRequest(UbiCreateSessionResponse session, String url, Class<T> responseClass) throws UbiApiErrorResponseException, UbiHardApiException {
        return authorizedGetRequest(session, url, responseClass, null);
    }

    private <T> T authorizedGetRequest(UbiCreateSessionResponse session, String url, Class<T> responseClass, Map<String, String> headers) throws UbiApiErrorResponseException, UbiHardApiException {
        final var builder = authorizedRequestBuilder(url, session)
                .get();
        // Add headers
        if (headers != null) {
            headers.forEach(builder::header);
        }

        Request request = builder
                .build();
        log.trace("Request: " + request.toString());
        try (Response response = client.newCall(request).execute()) {
            // Once the request comes back, let's check if it has any errors...
            final String responseStr = validateAndUnpackResponse(response);

            // Unpack response
            ResponseBody responseBody = response.body();
            System.out.println(responseBody);
            System.out.println(responseStr);
            final var responseString = responseBody == null ? null : responseStr;

            log.debug("Response body: " + responseString);
            return gson.fromJson(responseString, responseClass);
        } catch (IOException e) {
            log.error("Unable to execute request: " + e.getMessage(), e);
            throw new UbiHardApiException("Failure in request: " + e.getMessage(), e);
        }
    }

    /**
     * This checks if the response contains errors.
     * In many cases the API response with an error,
     * we try to unpack this error message whenever possible.
     * <p>
     * If we can't extract the API error itself, we fail with an hard error.
     *
     * @param response
     * @throws UbiHardApiException          When there is a sever failure with the request
     * @throws UbiApiErrorResponseException When there was an API error with a proper API-Error object given
     */
    private String validateAndUnpackResponse(Response response) throws UbiHardApiException, UbiApiErrorResponseException {
        // If for whatever reason we get a non-200 status code, lets see if the object we got back is an error object.
        // Let's try to decode the error
        ResponseBody responseBody = response.body();
        String responseBodyStr = null;
        try {
            responseBodyStr = responseBody.string();
        } catch (IOException e) {
            log.warn("Unable to read responseBody as string");
        }
        if (response.code() != 200) {
            // TODO Decode soft JSON errors?
            log.error("Hard API-Response failure - responseBody: " + responseBodyStr);
            throw new UbiHardApiException("Hard API-Request failure (code: " + response.code() + "): ");
        }
        return responseBodyStr;
    }


    /**
     * Login with given credentials to retrieve a new ticket
     *
     * @param username Username
     * @param password Password
     * @return Session repsonse
     * @throws UbiHardApiException
     * @throws UbiApiErrorResponseException
     */
    public UbiCreateSessionResponse createSession(String username, String password) throws UbiHardApiException, UbiApiErrorResponseException {
        log.info("Creating new session in order to obtain new ticket (using credentials: '"
                + username + "' and password '" + password.substring(0, 3) + "[redacted]'");
        String credential = Credentials.basic(username, password);
        RequestBody body = RequestBody.create("{rememberMe: true}", MEDIA_TYPE_JSON_UTF8);
        Request request = new Request.Builder()
                .url(UbiWebserviceConsts.Urls.SESSION)
                .header("Ubi-AppId", UbiWebserviceConsts.UBI_APPID_VALUE)
                .header("Authorization", credential)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseStr = validateAndUnpackResponse(response);
            // TODO Check response code
            return gson.fromJson(responseStr, UbiCreateSessionResponse.class);
        } catch (IOException e) {
            log.error("Unable to execute request: " + e.getMessage(), e);
            throw new UbiHardApiException("API-Request Error: " + e.getMessage(), e);
        }
    }

    /**
     * Determines if the given session is usable for API requests by requesting the profile page.
     * This should not be called for each request and only be used as a health check.
     *
     * @param session The session to use
     * @return true when the session is good
     */
    public boolean isSessionValid(UbiCreateSessionResponse session) {
        if (session == null) {
            log.warn("Given session is null");
            return false;
        }
        UbiProfileResponse profile = null;
        try {
            profile = getProfile(session.getProfileId(), session);
            return profile.getAccountIssues() == null || !profile.getAccountIssues();
        } catch (UbiApiErrorResponseException e) {
            log.warn("Session validation failed with soft error response: " + e.getMessage());
        } catch (UbiHardApiException e) {
            log.warn("Session validation failed with hard error response: " + e.getMessage());
        }
        return false;
    }


    /**
     * Request the profile of an user
     * This is usually used to validate the login of the user.
     * <p>
     * <code>scraped URL = https://public-ubiservices.ubi.com/v3/users/<!--uuid--></code>
     *
     * @param uuid    UUID of the logged-in user.
     * @param session Session
     * @return UbiProfileResponse The user's profile.
     */
    public UbiProfileResponse getProfile(String uuid, @NotNull UbiCreateSessionResponse session) throws UbiApiErrorResponseException, UbiHardApiException {
        String url = UbiWebserviceConsts.Urls.USERS + uuid;
        return authorizedGetRequest(session, url, UbiProfileResponse.class);
    }

    public UbiPlayerResponse getPlayerOverview(String uuid, @NotNull UbiCreateSessionResponse session) throws UbiApiException {
        String url = MessageFormat.format(UbiWebserviceConsts.Urls.SPACES + "{0}"
                        + "/sandboxes/{1}/r6karma/players?board_id={2}&season_id={3}&region_id={4}&profile_ids={5}",
                UbiSpace.PC.getUbiInternalName(),
                UbiPlatform.PC.getUbiInternalName(),
                "pvp_ranked", // board_id
                -1, // season_id
                "ncsa", // region
                uuid);
        return authorizedGetRequest(session, url, UbiPlayerResponse.class);
    }

    public UbiSeasonalSummaryResponse getSeasonalSummary(String uuid, @NotNull UbiCreateSessionResponse session) throws UbiApiErrorResponseException, UbiHardApiException {
        // https://r6s-stats.ubisoft.com/v1/seasonal/summary/21e4e8e4-b70a-4f8a-be4d-d0db7c8c9076?gameMode=all,ranked,casual,unranked&platform=PC
        String url = "https://r6s-stats.ubisoft.com/v1/seasonal/summary/" + uuid + "?gameMode=all,ranked,casual,unranked&platform=PC";
        Map<String, String> headers = Map.of(
                "expiration", session.getExpiration().toString()
        );
        return authorizedGetRequest(session, url, UbiSeasonalSummaryResponse.class, headers);
    }

    /**
     * Fetch queue data
     * <p>
     * See: https://github.com/billy-yoyo/RainbowSixSiege-Python-API/blob/32da4403cfd529a39c41f21fe4e71443c4b4a487/r6sapi/platforms.py#L31
     *
     * @param uuid
     * @param session
     * @throws UbiApiErrorResponseException
     * @throws UbiHardApiException
     */
    public UbiOverallQueueStats getOverallQueueStats(String uuid, UbiCreateSessionResponse session) throws UbiApiErrorResponseException, UbiHardApiException {
        final var rankQueuesToFetch = List.of("rankedpvp_matchwon", "rankedpvp_matchlost", "rankedpvp_timeplayed",
                "rankedpvp_matchplayed", "rankedpvp_kills", "rankedpvp_death", "rankedpvp_assist");
        String url = MessageFormat
                .format("https://public-ubiservices.ubi.com/v1/spaces/{0}/sandboxes/{1}/playerstats2/statistics?populations={2}&statistics={3}",
                        UbiSpace.PC.getUbiInternalName(),
                        UbiPlatform.PC.getUbiInternalName(),
                        uuid,
                        String.join(",", rankQueuesToFetch));
        return authorizedGetRequest(session, url, UbiOverallQueueStats.class);
    }

    public void getTest(UbiCreateSessionResponse session) throws UbiApiErrorResponseException, UbiHardApiException{
        log.info(authorizedGetRequest(session,
                        "https://public-ubiservices.ubi.com/v1/spaces/5172a557-50b5-4665-b7db-e3f2e8c5041d/sandboxes/OSBOR_PC_LNCH_A/r6karma/player_skill_records?board_ids=pvp_ranked&season_ids=-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11,-12,-13,-14,-15,-16,-17,-18,-19,-20,-21,-22,-23,-24&region_ids=ncsa&profile_ids=a94d182e-3578-4d39-b6b6-0b4e3d1b3c22",
                String.class));
    }


    // MAPS: https://r6s-stats.ubisoft.com/v1/current/maps/a84634db-fc13-4ce7-b75f-5b8583f880ea?gameMode=all,ranked,casual,unranked&platform=PC&teamRole=all,attacker,defender&startDate=20200726&endDate=20201124

}
