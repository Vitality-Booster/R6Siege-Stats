package com.example.r6siegestats.r6sstatsapi.session;

import com.example.r6siegestats.r6sstatsapi.external.objects.respones.UbiCreateSessionResponse;
import lombok.Data;

@Data
public class SessionEntry {
    private String username;
    private String password;
    private UbiCreateSessionResponse ubiSession;
    private boolean active = false;

    public boolean isUsable() {
        if (ubiSession == null) {
            return false;
        }
        return true;
    }

}
