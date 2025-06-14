package com.codepushsdk.react;

import android.content.Context;

import androidx.annotation.Nullable;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodePush implements ReactPackage {

    private static final String BUNDLE_FILE_NAME = "main.jsbundle";
    private static final String CODE_PUSH_DIR_NAME = "CodePush";
    private static final String DEFAULT_SERVER_URL = "http://192.168.191.21:3000";

    private String mDeploymentKey;
    private Context mContext;
    private boolean mIsDebugMode;
    private String mServerUrl;
    private Integer mPublicKeyResourceDescriptor;

    private static boolean sIsUsingTestConfiguration = false;
    private static String sServiceUrl = DEFAULT_SERVER_URL;

    public CodePush() {
        // Required default constructor
    }

    public CodePush(String deploymentKey, Context context, boolean isDebugMode,
                    String serverUrl, Integer publicKeyResourceDescriptor) {
        this.mDeploymentKey = deploymentKey;
        this.mContext = context != null ? context.getApplicationContext() : null;
        this.mIsDebugMode = isDebugMode;
        this.mServerUrl = serverUrl;
        this.mPublicKeyResourceDescriptor = publicKeyResourceDescriptor;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        // modules.add(new CodePushNativeModule(reactContext, this));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    public static String getServiceUrl() {
        return sServiceUrl;
    }

    public static void setServiceUrl(String serviceUrl) {
        sServiceUrl = serviceUrl;
    }

    public static boolean isUsingTestConfiguration() {
        return sIsUsingTestConfiguration;
    }

    public static void setIsUsingTestConfiguration(boolean isUsingTestConfiguration) {
        sIsUsingTestConfiguration = isUsingTestConfiguration;
    }

    public static String getJSBundleFile(Context context) {
        File codePushDir = new File(context.getFilesDir(), CODE_PUSH_DIR_NAME);
        File bundleFile = new File(codePushDir, BUNDLE_FILE_NAME);
        return bundleFile.getAbsolutePath();
    }

    public static String getBundlePath(Context context) {
        return new File(context.getFilesDir(), CODE_PUSH_DIR_NAME).getAbsolutePath();
    }

    @Nullable
    public String getDeploymentKey() {
        return mDeploymentKey;
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }

    public boolean isDebugMode() {
        return mIsDebugMode;
    }

    @Nullable
    public String getServerUrl() {
        return mServerUrl;
    }

    @Nullable
    public Integer getPublicKeyResourceDescriptor() {
        return mPublicKeyResourceDescriptor;
    }
}
