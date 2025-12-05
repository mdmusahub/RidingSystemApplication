package com.mecaps.ridingBookingSystem.util;

public class DistanceFareUtil {
    private static final double EARTH_RADIUS_KM = 6371.00;
    private static final double COST_PER_KM = 10.00;

    /**
     * Calculates distance between two latitude/longitude points using Haversine formula.
     */
    public static Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert degrees to radians
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Distance in kilometers
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates total fare based on distance.
     */
    public static Double calculateFare(double distance){
        return COST_PER_KM * distance;
    }

    /**
     * Combined method to calculate fare directly from pickup and drop coordinates.
     */
    public static Double calculateFare(double pickupLat, double pickupLng, double dropLat, double dropLng) {
        double distance = calculateDistance(pickupLat, pickupLng, dropLat, dropLng);
        return calculateFare(distance);
    }
}