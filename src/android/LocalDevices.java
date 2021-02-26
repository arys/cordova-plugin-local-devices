package com.arystankaliakparov.cordova_plugin_local_devices;

import android.graphics.Bitmap;
import android.util.Log;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

public class LocalDevices extends CordovaPlugin {

    final static String TAG = "LocalDevicesTag";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals("scan")) {
            int timeout = data.optInt(0, 400);
            JSONArray types = data.optJSONArray(1);
            String subnet = Utils.getSubnetAddress(Utils.getIPAddress(true));
            scan(subnet, callbackContext, timeout, types);
            return true;
        }
        if (action.equals("printEscPosHtml")) {
            String html = data.getString(0);
            Gson gson = new Gson();
            Device device = gson.fromJson(data.getJSONObject(1).toString(), Device.class);
            printEscPosHtml(callbackContext, html, device);
            return true;
        }
        return false;
    }

    PluginResult generateProgressResult(JSONArray devices) throws JSONException {
        JSONObject systemPrinterResult = new JSONObject();
        systemPrinterResult.put("data", devices);
        systemPrinterResult.put("state", "progress");
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, systemPrinterResult);
        pluginResult.setKeepCallback(true);
        return pluginResult;
    }

    void scan(String subnet, CallbackContext callbackContext, int timeout, JSONArray types) {
        Gson gson = new Gson();
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                JSONArray devices = new JSONArray();
                try {
                    devices.put(new JSONObject(gson.toJson(new Device(null, "Android Printer", RecognizeDevice.DEVICE_TYPE_SYSTEM))));
                    callbackContext.sendPluginResult(generateProgressResult(devices));
                    for (int i = 1; i < 255; i++){
                        String ip = subnet + "." + i;
                        if (InetAddress.getByName(ip).isReachable(timeout)) {
                            RecognizeDevice recognizer = new RecognizeDevice(ip, types);
                            Device device = recognizer.recognize();
                            devices.put(new JSONObject(gson.toJson(device)));
                            callbackContext.sendPluginResult(generateProgressResult(devices));
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                JSONObject result = new JSONObject();
                try {
                    result.put("data", devices);
                    result.put("state", "completed");
                    callbackContext.success(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }

    void printEscPosHtml(CallbackContext callbackContext, String html, Device device) {
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int width = device.getWidthInPixels();
                Bitmap bitmap = new Html2Bitmap.Builder().setContext(cordova.getContext()).setContent(WebViewContent.html(html)).setBitmapWidth(width).build().getBitmap();
                try {
                    TcpConnection tcpConnection = new TcpConnection(device.ip, 9100);
                    EscPosPrinter printer = new EscPosPrinter(tcpConnection, 203, device.getWidthInPixels(), 32);
                    if (device.settings.cash_drawer) {
                        printer.printFormattedTextAndOpenCashBox("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmap) + "</img>\n", printer.getPrinterWidthMM());
                    } else {
                        printer.printFormattedText("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmap) + "</img>\n", printer.getPrinterWidthMM());
                    }
                    if (device.settings.bell) {
                        tcpConnection.write(Utils.hexStringToByteArray("1B420403"));
                    }
                    printer.disconnectPrinter();
                    callbackContext.success();
                } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {
                    e.printStackTrace();
                    callbackContext.error(e.getMessage());
                }
            }
        });
    }
}
