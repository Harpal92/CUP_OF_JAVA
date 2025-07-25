package com.example.intrack;

public class DistanceCalculator {
    public static double calculateDistance(int rssi, double rssiAtOneMeter, double pathLossExponent) {
        return Math.pow(10.0, (rssiAtOneMeter - rssi) / (10 * pathLossExponent));
    }
}
