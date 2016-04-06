package com.sensefi.izooclient.view;

import android.util.Log;

/**
 * Created by boobathiayyasamy on 05/04/16.
 */
public class SettingsView {
    private int id;
    private String ipAddress;
    private String port;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        String url = null;
        if(ipAddress != null && port != null) {
            url = new StringBuffer("http://").append(ipAddress).append(":").append(port)
                    .append("/iZooService/IZOO/SERVICE/").toString();
        }
        Log.d("Url:",url);
        return url;
    }
}
