package com.arystankaliakparov.cordova_plugin_local_devices;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DeviceSettings {
    public static final Map<Integer, String> PAPER_WIDTH_TO_PIXELS = createMapWidthToPixels();

    private static Map<Integer, String> createMapWidthToPixels() {
        Map<Integer, String> result = new HashMap<>();
        result.put(80, "274");
        result.put(58, "192");
        result.put(48, "164");
        return Collections.unmodifiableMap(result);
    }

    @Nullable
    public Integer paper_width;
    @Nullable
    public Boolean bell;
    @Nullable
    public Boolean cash_drawer;
}
