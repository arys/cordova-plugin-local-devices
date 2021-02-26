package com.arystankaliakparov.cordova_plugin_local_devices;

import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java9.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RecognizeDevice {
    public static final String DEVICE_TYPE_ESCPOS = "printer-escpos";
    public static final String DEVICE_TYPE_SYSTEM = "printer-system";
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
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return new Device(host, "Unrecognized", DEVICE_TYPE_UNRECOGNIZED);
    }

    private Device isESCPOS() {
        TcpConnection tcpConnection = new TcpConnection(host, 9100);
        try {
            tcpConnection.connect();
            tcpConnection.disconnect();
            return new Device(host, "ESC/POS", DEVICE_TYPE_ESCPOS);
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        }
        return new Device(host, "Unrecognized", DEVICE_TYPE_UNRECOGNIZED);
    }

}
