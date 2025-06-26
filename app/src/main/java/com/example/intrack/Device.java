package com.example.intrack;

public class Device {
    private String name;
    private String address;
    private int rssi; // for signal strength
    private double distance;

    public Device(String name, String address, int rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.distance=calculateDistance(rssi);
    }
     private  double calculateDistance(int rssi){
        int txpower=-59 ;// typical value,or customize per beacon
        return Math.pow(10,(txpower-rssi)/20.0) ;
     }
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }
    public double getDistance(){ return distance;}

    // Use only address for equality to avoid duplicates
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Device device = (Device) obj;
        return address.equals(device.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
