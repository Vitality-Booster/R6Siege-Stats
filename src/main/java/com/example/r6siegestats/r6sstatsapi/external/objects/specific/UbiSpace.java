package com.example.r6siegestats.r6sstatsapi.external.objects.specific;

/**
 * Ubisoft uses different spaces for different platforms.
 */
public enum UbiSpace {

    // "uplay": "5172a557-50b5-4665-b7db-e3f2e8c5041d",
    // "psn": "05bfb3f7-6c21-4c42-be1f-97a33fb5cf66",
    // "xbl": "98a601e5-ca91-4440-b1c5-753f601a2c90"
    PC("pc", "5172a557-50b5-4665-b7db-e3f2e8c5041d"),
    PSN("psn", "05bfb3f7-6c21-4c42-be1f-97a33fb5cf66"),
    XBL("xbl", "98a601e5-ca91-4440-b1c5-753f601a2c90");

    UbiSpace(String name, String ubiInternalUuid) {
        this.name = name;
        this.ubiInternalName = ubiInternalUuid;
    }


    String name;

    public String getName() {
        return name;
    }

    String ubiInternalName;

    public String getUbiInternalName() {
        return ubiInternalName;
    }

}
