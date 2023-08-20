package com.example.googlemap;

public class LocationHelper {
    private double Longitude;
    private double Latitude;

    public LocationHelper(double latitude, double longitude) {
        Latitude = latitude;
        Longitude=longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
