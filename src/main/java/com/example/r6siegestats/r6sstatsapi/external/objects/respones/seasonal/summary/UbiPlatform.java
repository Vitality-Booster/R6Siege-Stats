package com.example.r6siegestats.r6sstatsapi.external.objects.respones.seasonal.summary;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import java.util.Map;

@Data
public class UbiPlatform {

    @SerializedName("gameModes")
    private Map<String, UbiGameMode> gameModes;

}
