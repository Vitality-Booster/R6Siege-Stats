package com.example.r6siegestats.r6sstatsapi.external.objects.specific;

/**
 * Different platforms have internally different names, this is our mapping for them.
 */
public enum UbiPlatform {
    // "pc": "OSBOR_PC_LNCH_A",
    // "psn": "OSBOR_PS4_LNCH_A",
    // "xbl": "OSBOR_XBOXONE_LNCH_A"
    PC("pc", "OSBOR_PC_LNCH_A"),
    PSN("psn", "OSBOR_PS4_LNCH_A"),
    XBL("xbl", "OSBOR_XBOXONE_LNCH_A");

    UbiPlatform(String name, String ubiInternalName) {
        this.name = name;
        this.ubiInternalName = ubiInternalName;
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
