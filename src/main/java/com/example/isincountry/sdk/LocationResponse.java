package com.example.isincountry.sdk;

/**
 * Response model for location checking API
 */
public class LocationResponse {
    private boolean inside;
    private String message;
    private String countryCode;
    private double latitude;
    private double longitude;

    public LocationResponse() {}

    public LocationResponse(boolean inside, String message, String countryCode, double latitude, double longitude) {
        this.inside = inside;
        this.message = message;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
