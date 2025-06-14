package com.codepushsdk.react;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.*;
import com.facebook.react.module.annotations.ReactModule;

import java.net.MalformedURLException;

@ReactModule(name = CodePushModule.MODULE_NAME)
public class CodePushModule extends ReactContextBaseJavaModule {
    public static final String MODULE_NAME = "CodePush";
    private static final String TAG = "CodePushModule";

    private final CodePush codePush;

    public CodePushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(TAG, "Initializing CodePushModule");

        String deploymentKey = getDeploymentKey(reactContext);
        Log.d(TAG, "Using deployment key: " + (deploymentKey != null ? deploymentKey.substring(0, Math.min(deploymentKey.length(), 8)) + "..." : "null"));

        this.codePush = new CodePushBuilder(deploymentKey, reactContext)
                .setIsDebugMode(isDebugMode())
                .setServerUrl("http://192.168.191.21:3000")
                .build();

        Log.d(TAG, "CodePushModule initialized successfully");
    }

    private boolean isDebugMode() {
        try {
            Class<?> buildConfigClass = Class.forName("thc.mobile.BuildConfig");
            return (Boolean) buildConfigClass.getField("DEBUG").get(null);
        } catch (Exception e) {
            Log.w(TAG, "Could not access BuildConfig.DEBUG, defaulting to false", e);
            return false;
        }
    }

    private String getDeploymentKey(ReactApplicationContext context) {
        try {
            int resId = context.getResources().getIdentifier("code_push_deployment_key", "string", context.getPackageName());
            if (resId != 0) {
                return context.getResources().getString(resId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving deployment key", e);
        }
        return "staging_deployment_key_abc123";
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    private void resolveOrReject(Promise promise, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage(), e);
            promise.reject("CodePushError", e.getMessage());
        }
    }

    @ReactMethod
    public void getConfiguration(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(codePush.getDeploymentKey()));
    }

    @ReactMethod
    public void notifyApplicationReady(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void isFailedUpdate(String packageHash, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(false));
    }

    @ReactMethod
    public void getUpdateMetadata(int updateState, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(null));
    }

    @ReactMethod
    public void restartApp(boolean onlyIfUpdateIsPending, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void getNewStatusReport(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(null));
    }

    @ReactMethod
    public void recordStatusReported(ReadableMap statusReport, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void saveStatusReportForRetry(ReadableMap statusReport, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void getLatestRollbackInfo(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(null));
    }

    @ReactMethod
    public void setLatestRollbackInfo(String packageHash, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void disallow(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void allow(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void clearUpdates(Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(true));
    }

    @ReactMethod
    public void isFirstRun(String packageHash, Promise promise) {
        resolveOrReject(promise, () -> promise.resolve(false));
    }
}
