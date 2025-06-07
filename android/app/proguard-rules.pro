# Preserve React Native core classes
-keep class com.facebook.react.** { *; }
-keepclassmembers class com.facebook.react.** { *; }
-dontwarn com.facebook.react.**

# Preserve custom CodePush classes
-keep class com.codepushsdk.react.** { *; }
-keepclassmembers class com.codepushsdk.react.** { *; }
-dontwarn com.codepushsdk.react.**

# Preserve Nimbus JOSE JWT
-keep class com.nimbusds.jose.** { *; }
-keepclassmembers class com.nimbusds.jose.** { *; }
-dontwarn com.nimbusds.jose.**

# Preserve ReactInstanceManager for bundle loading
-keepclassmembers class com.facebook.react.ReactInstanceManager {
    private final ** mBundleLoader;
}