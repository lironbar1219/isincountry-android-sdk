package com.example.isincountry.sdk;

import com.google.gson.annotations.SerializedName;

/**
 * Request model for location checking API
 */
public class LocationRequest {
    private double latitude;
    private double longitude;

    @SerializedName("country_code")
    private String countryCode;

    public LocationRequest(double latitude, double longitude, String countryCode) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
