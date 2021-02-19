package com.arystankaliakparov.cordova_plugin_local_devices;

import android.util.Log;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalDevices extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("scan")) {
            String ip = Utils.getIPAddress(true);
            String[] ipSplit = ip.split("\\.");
            String subnet = ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2];
            scan(subnet, callbackContext);
            return true;
        }

        return false;
    }

    void scan(String subnet, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                JSONArray ips = new JSONArray();
                try {
                    for (int i = 1; i < 255; i++){
                        String ip = subnet + "." + i;
                        if (InetAddress.getByName(ip).isReachable(5)){
                            ips.put(ip);
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callbackContext.success(ips);
            }
        });
    }
}
