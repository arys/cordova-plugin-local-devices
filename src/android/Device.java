package com.arystankaliakparov.cordova_plugin_local_devices;

import androidx.annotation.Nullable;

public class Device {
    public String id;
    public String ip;
    public String name;
    public String type;

    @Nullable
    public DeviceSettings settings = null;

    Device(String ip, String name, String type) {
        this.id = type + "-" + ip;
        this.ip = ip;
        this.name = name;
        this.type = type;
    }

    public int getWidthInPixels() {
        Integer paperWidth = settings.paper_width != null ? settings.paper_width : 80;
        return Integer.parseInt(DeviceSettings.PAPER_WIDTH_TO_PIXELS.get(paperWidth));
    }
}
