package com.arystankaliakparov.cordova_plugin_local_devices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java9.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RecognizeDevice {
    public static final String DEVICE_TYPE_ESCPOS = "printer-escpos";
    public static final String DEVICE_TYPE_UNRECOGNIZED = "unrecognized";

    String host;
    JSONArray deviceTypesToRecognize;

    RecognizeDevice(String host, JSONArray deviceTypesToRecognize) {
        if (deviceTypesToRecognize == null) {
            JSONArray defaultTypes = new JSONArray();
            defaultTypes.put(DEVICE_TYPE_ESCPOS);
            this.deviceTypesToRecognize = defaultTypes;
        } else {
            this.deviceTypesToRecognize = deviceTypesToRecognize;
        }
        this.host = host;
    }

    public Device recognize() throws JSONException {
        List<CompletableFuture<Device>> futures = new ArrayList<>();
        futures.add(CompletableFuture.supplyAsync(this::unrecognizedDevice));

        for (int i = 0; i < deviceTypesToRecognize.length(); i++) {
            if (deviceTypesToRecognize.getString(i).equals(DEVICE_TYPE_ESCPOS)) {
                futures.add(CompletableFuture.supplyAsync(this::isESCPOS));
            }
        }

        CompletableFuture<Device>[] futuresArray = futures.toArray(new CompletableFuture[futures.size()]);
        CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(futuresArray);

        try {
            return (Device) anyOfFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return new Device(host, "Unrecognized", DEVICE_TYPE_UNRECOGNIZED);
    }

    private Device unrecognizedDevice() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return new Device(host, "Unrecognized", DEVICE_TYPE_UNRECOGNIZED);
    }

    private Device isESCPOS() {
        Socket client = null;
        try {
            SocketAddress address = new InetSocketAddress(host, 9100);
            client = new Socket();
            client.connect(address, 5000);
            client.close();
            return new Device(host, "ESC/POS", DEVICE_TYPE_ESCPOS);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Device(host, "Unrecognized", DEVICE_TYPE_UNRECOGNIZED);
    }

}
