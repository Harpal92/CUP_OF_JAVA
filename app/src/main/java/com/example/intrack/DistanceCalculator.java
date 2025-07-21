package com.example.intrack;

public class DistanceCalculator {

    // This method takes 3 values:
    // 1. The live RSSI from the scan
    // 2. The reference RSSI at 1 meter
    // 3. The environment factor (n)
    public static double calculateDistance(int rssi, int rssiAtOneMeter, double pathLossExponent) {
        return Math.pow(10.0, ((rssiAtOneMeter - rssi) / (10.0 * pathLossExponent)));
    }
}
