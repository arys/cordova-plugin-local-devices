package com.arystankaliakparov.cordova_plugin_local_devices;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalDevices extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("greet")) {
            String name = data.getString(0);
            String message = "Hello, " + name;
            callbackContext.success(message);
            return true;
        }

        if (action.equals("scan")) {
            ArrayList<String> ipList = new ArrayList<>();
            String ip = Utils.getIPAddress(true);
            String[] ipSplit = ip.split("\\.");
            String subnet = ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2];

            ExecutorService myExecutor = Executors.newCachedThreadPool();
            myExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    JSONArray ips = scan(subnet);
                    callbackContext.success(ips);
                }
            });
        }

        return false;
    }

    JSONArray scan(String subnet) {
        JSONArray ips = new JSONArray();
        try {
            for (int i = 1; i < 255; i++){
                String ip = subnet + "." + i;
                if (InetAddress.getByName(ip).isReachable(500)){
                    ips.put(ip);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ips;
    }
}
