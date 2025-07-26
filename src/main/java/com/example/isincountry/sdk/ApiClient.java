package com.example.isincountry.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * API Client for communicating with the IsInCountry server
 */
public class ApiClient {
    private static final String ENDPOINT_CHECK_LOCATION = "/api/v1/check";
    private static final int TIMEOUT_SECONDS = 30;

    private OkHttpClient httpClient;
    private Gson gson;
    private String serverUrl;

    public ApiClient() {
        setupHttpClient();
        setupGson();
    }

    private void setupHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    private void setupGson() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
    }

    /**
     * Check if coordinates are within a country
     * @param request Location request containing coordinates and country code
     * @param callback Callback to receive the result
     */
    public void checkLocation(LocationRequest request, IsInCountrySDK.LocationCheckCallback callback) {
        if (serverUrl == null) {
            callback.onError("Server URL not set");
            return;
        }

        String json = gson.toJson(request);
        RequestBody requestBody = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request httpRequest = new Request.Builder()
                .url(serverUrl + ENDPOINT_CHECK_LOCATION)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();

                        // Parse the server response format
                        ServerResponse serverResponse = gson.fromJson(responseBody, ServerResponse.class);

                        if (serverResponse != null && serverResponse.success) {
                            boolean isInside = serverResponse.data.is_inside_country;
                            String message = "Country: " + serverResponse.data.country_name +
                                           "\nCoordinates: " + serverResponse.data.latitude + ", " + serverResponse.data.longitude +
                                           "\nChecked at: " + serverResponse.data.checked_at;
                            callback.onResult(isInside, message);
                        } else {
                            String error = serverResponse != null ? serverResponse.error : "Unknown error";
                            callback.onError("Server error: " + error);
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        callback.onError("Server error (" + response.code() + "): " + errorBody);
                    }
                } catch (Exception e) {
                    callback.onError("Response parsing error: " + e.getMessage());
                } finally {
                    response.close();
                }
            }
        });
    }

    // Inner classes to match server response format
    private static class ServerResponse {
        boolean success;
        ServerData data;
        String error;
    }

    private static class ServerData {
        boolean is_inside_country;
        double latitude;
        double longitude;
        String country_code;
        String country_name;
        String checked_at;
    }
}
