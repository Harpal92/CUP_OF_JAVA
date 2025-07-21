package com.example.intrack;

import java.io.Serializable;

public class Device implements Serializable {
    private String name;
    private String address;
    private int rssi;
    private double distance;

    public Device(String name, String address, int rssi, double distance) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.distance = distance;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public int getRssi() { return rssi; }
    public double getDistance() { return distance; }
}
