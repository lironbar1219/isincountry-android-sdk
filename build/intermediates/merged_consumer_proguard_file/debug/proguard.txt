# Keep all public classes and methods in the SDK
-keep public class com.example.isincountry.sdk.** { *; }

# Keep OkHttp classes
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Keep Google Play Services Location classes
-keep class com.google.android.gms.location.** { *; }
-dontwarn com.google.android.gms.location.**
