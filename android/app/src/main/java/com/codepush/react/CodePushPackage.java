package com.codepushsdk.react;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.ReactApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CodePushPackage implements ReactPackage {
    private CodePush mCodePush;

    // Default constructor required for autolinking
    public CodePushPackage() {
        this.mCodePush = null;
    }

    // Constructor with CodePush parameter for manual initialization
    public CodePushPackage(CodePush codePush) {
        this.mCodePush = codePush;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        
        // Create CodePushModule - it will handle its own CodePush initialization
        modules.add(new CodePushModule(reactContext));
        
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
    
    // Getter for CodePush instance (if needed)
    public CodePush getCodePush() {
        return mCodePush;
    }
    
    // Setter for CodePush instance (if needed)
    public void setCodePush(CodePush codePush) {
        this.mCodePush = codePush;
    }
}