package com.codepushsdk.react;

import android.content.Context;

import androidx.annotation.Nullable;

public class CodePushBuilder {
    private final String mDeploymentKey;
    private final Context mContext;
    private boolean mIsDebugMode = false;
    private String mServerUrl;
    private Integer mPublicKeyResourceDescriptor;

    public CodePushBuilder(String deploymentKey, Context context) {
        this.mDeploymentKey = deploymentKey;
        this.mContext = context.getApplicationContext();
        this.mServerUrl = CodePush.getServiceUrl(); // default
    }

    public CodePushBuilder setIsDebugMode(boolean isDebugMode) {
        this.mIsDebugMode = isDebugMode;
        return this;
    }

    public CodePushBuilder setServerUrl(@Nullable String serverUrl) {
        if (serverUrl != null) this.mServerUrl = serverUrl;
        return this;
    }

    public CodePushBuilder setPublicKeyResourceDescriptor(int publicKeyResourceDescriptor) {
        this.mPublicKeyResourceDescriptor = publicKeyResourceDescriptor;
        return this;
    }

    public CodePush build() {
        return new CodePush(
            this.mDeploymentKey,
            this.mContext,
            this.mIsDebugMode,
            this.mServerUrl,
            this.mPublicKeyResourceDescriptor
        );
    }
}
