package com.example.r6siegestats.r6sstatsapi.external.objects.respones;

import lombok.Data;

@Data
public class UbiCreateSessionResponse {
    public String platformType;
    public String ticket;
    public String spaceId;
    public String expiration;
    public String sessionId;
    public String sessionKey;
    public String rememberMeTicket;
    public String profileId;
    public String userId;
    public String clientIp;
    public String serverTime;
}
