package com.example.intrack;

import com.example.intrack.SensorPosition.Point;

import java.util.List;

public class Trilateration {

    public static Point calculatePosition(List<Point> positions, List<Double> distances) {
        if (positions.size() < 3 || distances.size() < 3) {
            return new Point(0, 0); // Not enough data
        }

        // Coordinates and distances
        Point p1 = positions.get(0);
        Point p2 = positions.get(1);
        Point p3 = positions.get(2);

        double d1 = distances.get(0);
        double d2 = distances.get(1);
        double d3 = distances.get(2);

        // Apply trilateration formula
        double A = 2 * (p2.x - p1.x);
        double B = 2 * (p2.y - p1.y);
        double C = Math.pow(d1, 2) - Math.pow(d2, 2) - Math.pow(p1.x, 2) + Math.pow(p2.x, 2) - Math.pow(p1.y, 2) + Math.pow(p2.y, 2);
        double D = 2 * (p3.x - p2.x);
        double E = 2 * (p3.y - p2.y);
        double F = Math.pow(d2, 2) - Math.pow(d3, 2) - Math.pow(p2.x, 2) + Math.pow(p3.x, 2) - Math.pow(p2.y, 2) + Math.pow(p3.y, 2);

        float x = (float) ((C * E - F * B) / (A * E - D * B));
        float y = (float) ((A * F - D * C) / (A * E - D * B));

        return new Point(x, y);
    }
}
