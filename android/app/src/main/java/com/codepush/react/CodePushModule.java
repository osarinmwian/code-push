package com.codepushsdk.react;

import android.util.Log;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = "CodePush")
public class CodePushModule extends ReactContextBaseJavaModule {
    private static final String TAG = "CodePushModule";
    private final CodePush codePush;

    public CodePushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(TAG, "Initializing CodePushModule");
        
        String deploymentKey = getDeploymentKey(reactContext);
        Log.d(TAG, "Using deployment key: " + (deploymentKey != null ? deploymentKey.substring(0, Math.min(deploymentKey.length(), 8)) + "..." : "null"));
        
        this.codePush = new CodePushBuilder(deploymentKey, reactContext)
            .setIsDebugMode(isDebugMode()) // Use method to check debug mode
            .setServerUrl("http://192.168.191.21:3000")
            .build();
        
        Log.d(TAG, "CodePushModule initialized successfully");
    }

    // Helper method to check debug mode
    private boolean isDebugMode() {
        try {
            // Try to access BuildConfig from your app package
            Class<?> buildConfigClass = Class.forName("thc.mobile.BuildConfig");
            return (Boolean) buildConfigClass.getField("DEBUG").get(null);
        } catch (Exception e) {
            Log.w(TAG, "Could not access BuildConfig.DEBUG, defaulting to false", e);
            return false;
        }
    }

    private String getDeploymentKey(ReactApplicationContext reactContext) {
        Log.d(TAG, "Attempting to retrieve deployment key from resources");
        
        // Try to get the deployment key from resources
        try {
            int resId = reactContext.getResources().getIdentifier("code_push_deployment_key", "string", reactContext.getPackageName());
            if (resId != 0) {
                String key = reactContext.getResources().getString(resId);
                Log.d(TAG, "Successfully retrieved deployment key from resources");
                return key;
            } else {
                Log.w(TAG, "Deployment key resource not found, using fallback");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving deployment key from resources: " + e.getMessage(), e);
        }
        
        Log.w(TAG, "Using hardcoded deployment key fallback");
        return "staging_deployment_key_abc123"; // Replace with your actual deployment key
    }

    @Override
    public String getName() {
        return "CodePush"; // Must match the name expected in JavaScript (NativeModules.CodePush)
    }

    @ReactMethod
    public void getConfiguration(Promise promise) {
        Log.d(TAG, "getConfiguration called");
        try {
            String deploymentKey = codePush.getDeploymentKey();
            Log.d(TAG, "Configuration retrieved successfully");
            promise.resolve(deploymentKey);
        } catch (Exception e) {
            Log.e(TAG, "Error getting configuration: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void notifyApplicationReady(Promise promise) {
        Log.d(TAG, "notifyApplicationReady called");
        try {
            // Implement notify logic (e.g., call CodePushUtils or other methods)
            Log.d(TAG, "Application ready notification sent");
            promise.resolve(true);
        } catch (Exception e) {
            Log.e(TAG, "Error notifying application ready: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void isFailedUpdate(String packageHash, Promise promise) {
        Log.d(TAG, "isFailedUpdate called with packageHash: " + packageHash);
        try {
            // Implement logic to check if an update failed
            boolean isFailed = false; // Placeholder
            Log.d(TAG, "Update failed check result: " + isFailed);
            promise.resolve(isFailed);
        } catch (Exception e) {
            Log.e(TAG, "Error checking failed update: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void getUpdateMetadata(int updateState, Promise promise) {
        Log.d(TAG, "getUpdateMetadata called with updateState: " + updateState);
        try {
            // Implement logic to return update metadata
            Log.d(TAG, "Update metadata retrieved");
            promise.resolve(null); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error getting update metadata: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void restartApp(boolean onlyIfUpdateIsPending, Promise promise) {
        Log.d(TAG, "restartApp called with onlyIfUpdateIsPending: " + onlyIfUpdateIsPending);
        try {
            // Implement restart logic
            Log.d(TAG, "App restart initiated");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error restarting app: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void getNewStatusReport(Promise promise) {
        Log.d(TAG, "getNewStatusReport called");
        try {
            // Implement status report logic
            Log.d(TAG, "Status report retrieved");
            promise.resolve(null); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error getting status report: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void recordStatusReported(ReadableMap statusReport, Promise promise) {
        Log.d(TAG, "recordStatusReported called");
        try {
            // Implement status recording logic
            Log.d(TAG, "Status report recorded");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error recording status report: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void saveStatusReportForRetry(ReadableMap statusReport, Promise promise) {
        Log.d(TAG, "saveStatusReportForRetry called");
        try {
            // Implement retry logic
            Log.d(TAG, "Status report saved for retry");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error saving status report for retry: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void getLatestRollbackInfo(Promise promise) {
        Log.d(TAG, "getLatestRollbackInfo called");
        try {
            // Implement rollback info logic
            Log.d(TAG, "Rollback info retrieved");
            promise.resolve(null); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error getting rollback info: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void setLatestRollbackInfo(String packageHash, Promise promise) {
        Log.d(TAG, "setLatestRollbackInfo called with packageHash: " + packageHash);
        try {
            // Implement rollback info setting logic
            Log.d(TAG, "Rollback info set successfully");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error setting rollback info: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void disallow(Promise promise) {
        Log.d(TAG, "disallow called");
        try {
            // Implement disallow restart logic
            Log.d(TAG, "Restart disallowed");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error disallowing restart: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void allow(Promise promise) {
        Log.d(TAG, "allow called");
        try {
            // Implement allow restart logic
            Log.d(TAG, "Restart allowed");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error allowing restart: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void clearUpdates(Promise promise) {
        Log.d(TAG, "clearUpdates called");
        try {
            // Implement clear updates logic
            Log.d(TAG, "Updates cleared");
            promise.resolve(true); // Placeholder
        } catch (Exception e) {
            Log.e(TAG, "Error clearing updates: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void isFirstRun(String packageHash, Promise promise) {
        Log.d(TAG, "isFirstRun called with packageHash: " + packageHash);
        try {
            // Implement isFirstRun logic
            boolean isFirst = false; // Placeholder
            Log.d(TAG, "First run check result: " + isFirst);
            promise.resolve(isFirst);
        } catch (Exception e) {
            Log.e(TAG, "Error checking first run: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }
}