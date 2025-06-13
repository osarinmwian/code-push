package com.codepushsdk.react;

import android.content.Context;

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
    
    // Instance variables for the builder pattern
    private String mDeploymentKey;
    private Context mContext;
    private boolean mIsDebugMode;
    private String mServerUrl;
    private Integer mPublicKeyResourceDescriptor;
    
    // Static configuration
    private static boolean sIsUsingTestConfiguration = false;
    private static String sServiceUrl = DEFAULT_SERVER_URL;

    // Default constructor (required by CodePushBuilder error)
    public CodePush() {
        // Default constructor
    }
    
    // Constructor expected by CodePushBuilder
    public CodePush(String deploymentKey, Context context, boolean isDebugMode, 
                   String serverUrl, Integer publicKeyResourceDescriptor) {
        this.mDeploymentKey = deploymentKey;
        this.mContext = context;
        this.mIsDebugMode = isDebugMode;
        this.mServerUrl = serverUrl;
        this.mPublicKeyResourceDescriptor = publicKeyResourceDescriptor;
    }

    // ReactPackage interface implementation
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        // Add your native modules here if any
        // modules.add(new CodePushNativeModule(reactContext));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    // Static methods expected by other classes
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

    // Existing methods
    public static String getJSBundleFile(Context context) {
        File codePushDir = new File(context.getFilesDir(), CODE_PUSH_DIR_NAME);
        File bundleFile = new File(codePushDir, BUNDLE_FILE_NAME);
        return bundleFile.getAbsolutePath();
    }

    public static String getBundlePath(Context context) {
        return new File(context.getFilesDir(), CODE_PUSH_DIR_NAME).getAbsolutePath();
    }
    
    // Getters for instance variables
    public String getDeploymentKey() {
        return mDeploymentKey;
    }
    
    public Context getContext() {
        return mContext;
    }
    
    public boolean isDebugMode() {
        return mIsDebugMode;
    }
    
    public String getServerUrl() {
        return mServerUrl;
    }
    
    public Integer getPublicKeyResourceDescriptor() {
        return mPublicKeyResourceDescriptor;
    }
}