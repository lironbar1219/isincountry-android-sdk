# IsInCountry Android SDK

An Android SDK for checking if a device's location is within a specific country.

## Features

- Real-time location detection
- Country boundary verification
- Simple API integration
- Lightweight and efficient

## Installation

Add this to your app's `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.yourusername:isincountry-android-sdk:1.0.0'
}
```

And add JitPack repository to your project's `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

## Usage

```java
// Initialize the SDK
IsInCountrySDK sdk = new IsInCountrySDK(context);

// Check if current location is in a specific country
sdk.checkLocation("US", new LocationCallback() {
    @Override
    public void onResult(LocationResponse response) {
        if (response.isInside()) {
            // Device is in the specified country
        } else {
            // Device is not in the specified country
        }
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

## Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

## Requirements

- Android API level 26 (Android 8.0) or higher
- Location permissions
- Internet connection

## License

MIT License - see LICENSE file for details.
