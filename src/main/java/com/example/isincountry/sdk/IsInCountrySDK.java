package com.example.isincountry.sdk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import java.util.Locale;

/**
 * IsInCountry SDK - Main class for checking if device location is within a specific country
 */
public class IsInCountrySDK {
    private static IsInCountrySDK instance;
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private ApiClient apiClient;
    private String serverUrl;

    private IsInCountrySDK(Context context) {
        this.context = context.getApplicationContext();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        this.apiClient = new ApiClient();
    }

    /**
     * Initialize the SDK with server URL
     * @param context Application context
     * @param serverUrl URL of the IsInCountry API server
     * @return SDK instance
     */
    public static IsInCountrySDK initialize(Context context, String serverUrl) {
        if (instance == null) {
            instance = new IsInCountrySDK(context);
        }
        instance.serverUrl = serverUrl;
        instance.apiClient.setServerUrl(serverUrl);
        return instance;
    }

    /**
     * Get the singleton instance
     * @return SDK instance
     */
    public static IsInCountrySDK getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SDK not initialized. Call initialize() first.");
        }
        return instance;
    }

    /**
     * Check if current device location is within the device's country
     * @param callback Callback to receive the result
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void checkCurrentLocationInDeviceCountry(LocationCheckCallback callback) {
        String countryCode = getDeviceCountryCode();
        if (countryCode == null) {
            callback.onError("Unable to determine device country");
            return;
        }
        checkCurrentLocationInCountry(countryCode, callback);
    }

    /**
     * Check if current device location is within a specific country
     * @param countryCode ISO country code (e.g., "US", "GB", "FR")
     * @param callback Callback to receive the result
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void checkCurrentLocationInCountry(String countryCode, LocationCheckCallback callback) {
        if (!hasLocationPermission()) {
            callback.onError("Location permission not granted");
            return;
        }

        getCurrentLocation(new LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                checkCoordinateInCountry(latitude, longitude, countryCode, callback);
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to get location: " + error);
            }
        });
    }

    /**
     * Check if specific coordinates are within a country
     * @param latitude Latitude coordinate
     * @param longitude Longitude coordinate
     * @param countryCode ISO country code
     * @param callback Callback to receive the result
     */
    public void checkCoordinateInCountry(double latitude, double longitude, String countryCode, LocationCheckCallback callback) {
        LocationRequest request = new LocationRequest(latitude, longitude, countryCode);
        apiClient.checkLocation(request, callback);
    }

    /**
     * Get current device location
     * @param callback Callback to receive location
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void getCurrentLocation(LocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onError("Location permission not granted");
            return;
        }

        @SuppressWarnings("MissingPermission")
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocationReceived(location.getLatitude(), location.getLongitude());
            } else {
                callback.onError("Unable to get current location");
            }
        }).addOnFailureListener(e -> callback.onError("Location error: " + e.getMessage()));
    }

    /**
     * Get device country code based on SIM card or network
     * @return ISO country code or null if unavailable
     */
    public String getDeviceCountryCode() {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                // Try SIM country first
                String simCountry = tm.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    return simCountry.toUpperCase();
                }

                // Fall back to network country
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) {
                    return networkCountry.toUpperCase();
                }
            }

            // Fall back to system locale
            return Locale.getDefault().getCountry();
        } catch (Exception e) {
            return Locale.getDefault().getCountry();
        }
    }

    /**
     * Check if location permission is granted
     * @return true if permission granted, false otherwise
     */
    public boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
               ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Callback interface for location results
     */
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
        void onError(String error);
    }

    /**
     * Callback interface for location check results
     */
    public interface LocationCheckCallback {
        void onResult(boolean isInCountry, String message);
        void onError(String error);
    }
}
