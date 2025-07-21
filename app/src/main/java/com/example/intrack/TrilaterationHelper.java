package com.example.intrack;

import java.util.List;

public class TrilaterationHelper {

    public static class Position {
        public float x;
        public float y;

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static Position calculatePosition(List<Device> devices) {
        if (devices.size() < 3) return new Position(500, 9800);  // Default fallback

        Device d1 = devices.get(0);
        Device d2 = devices.get(1);
        Device d3 = devices.get(2);

        SensorPosition.Point p1 = SensorPosition.getSensorPosition(d1.getAddress());
        SensorPosition.Point p2 = SensorPosition.getSensorPosition(d2.getAddress());
        SensorPosition.Point p3 = SensorPosition.getSensorPosition(d3.getAddress());

        if (p1 == null || p2 == null || p3 == null) return new Position(500, 9800);  // fallback

        double x1 = p1.x, y1 = p1.y, r1 = d1.getDistance();
        double x2 = p2.x, y2 = p2.y, r2 = d2.getDistance();
        double x3 = p3.x, y3 = p3.y, r3 = d3.getDistance();

        double A = 2 * (x2 - x1);
        double B = 2 * (y2 - y1);
        double C = r1 * r1 - r2 * r2 - x1 * x1 + x2 * x2 - y1 * y1 + y2 * y2;
        double D = 2 * (x3 - x2);
        double E = 2 * (y3 - y2);
        double F = r2 * r2 - r3 * r3 - x2 * x2 + x3 * x3 - y2 * y2 + y3 * y3;

        double denominator = (A * E - B * D);
        if (denominator == 0) return new Position((float)x1, (float)y1);  // fallback

        double x = (C * E - B * F) / denominator;
        double y = (A * F - C * D) / denominator;

        return new Position((float)x, (float)y);
    }
}
