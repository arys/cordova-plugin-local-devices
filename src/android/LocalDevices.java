package com.arystankaliakparov.cordova_plugin_local_devices;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

public class LocalDevices extends CordovaPlugin {

    static int ARG_INDEX_TIMEOUT = 0;
    static int ARG_INDEX_DEVICE_TYPES_TO_RECOGNIZE = 1;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("scan")) {
            JSONArray types = data.optJSONArray(ARG_INDEX_DEVICE_TYPES_TO_RECOGNIZE);
            int timeout = data.optInt(ARG_INDEX_TIMEOUT, 500);
            String subnet = Utils.getSubnetAddress(Utils.getIPAddress(true));
            scan(subnet, callbackContext, timeout, types);
            return true;
        }
        return false;
    }

    void scan(String subnet, CallbackContext callbackContext, int timeout, JSONArray types) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                JSONArray ips = new JSONArray();
                try {
                    for (int i = 1; i < 255; i++){
                        String ip = subnet + "." + i;
                        if (InetAddress.getByName(ip).isReachable(timeout)) {
                            RecognizeDevice recognizer = new RecognizeDevice(ip, types);
                            Device device = recognizer.recognize();

                            JSONObject deviceJson = new JSONObject();
                            deviceJson.put("ip", device.ip);
                            deviceJson.put("name", device.name);
                            deviceJson.put("type", device.type);
                            ips.put(deviceJson);

                            JSONObject result = new JSONObject();
                            result.put("data", ips);
                            result.put("state", "progress");

                            PluginResult res = new PluginResult(PluginResult.Status.OK, result);
                            res.setKeepCallback(true);
                            callbackContext.sendPluginResult(res);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                JSONObject result = new JSONObject();
                try {
                    result.put("data", ips);
                    result.put("state", "completed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callbackContext.success(result);
            }
        });
    }
}
