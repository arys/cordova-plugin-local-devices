package com.arystankaliakparov.cordova_plugin_local_devices;

public class Device {
    public String id;
    public String ip;
    public String name;
    public String type;

    public DeviceSettings settings = new DeviceSettings();

    Device(String ip, String name, String type) {
        this.id = type + "-" + ip;
        this.ip = ip;
        this.name = name;
        this.type = type;
    }

    public int getWidthInPixels() {
        return Math.round((settings.paper_width - 8) * ((float) settings.dpi) / DeviceSettings.INCH_TO_MM);
    }

    public void setSettings(DeviceSettings settings) {
        this.settings = settings;
    }
}
