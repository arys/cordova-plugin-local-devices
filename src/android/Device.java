package com.arystankaliakparov.cordova_plugin_local_devices;

public class Device {
    public String ip;
    public String name;
    public String type;

    Device(String ip, String name, String type) {
        this.ip = ip;
        this.name = name;
        this.type = type;
    }
}
