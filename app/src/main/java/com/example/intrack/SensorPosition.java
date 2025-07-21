package com.example.intrack;

import java.util.HashMap;
import java.util.Map;

public class SensorPosition {

    // Define a class to store coordinates
    public static class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    // Static map of sensor MAC addresses to pixel positions
    public static final Map<String, Point> SENSOR_POSITIONS = new HashMap<>();

    static {
        // Example MAC addresses (Replace with your real ones later)
        SENSOR_POSITIONS.put("00:11:22:33:44:01", new Point(100, 100));     // S1
        SENSOR_POSITIONS.put("00:11:22:33:44:02", new Point(900, 100));     // S2
        SENSOR_POSITIONS.put("00:11:22:33:44:03", new Point(900, 9800));    // S3
        SENSOR_POSITIONS.put("00:11:22:33:44:04", new Point(100, 9800));    // S4
    }

    public static Point getSensorPosition(String macAddress) {
        return SENSOR_POSITIONS.get(macAddress);
    }
}
