package com.codepushsdk.react;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class CodePushDialog extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME = "CodePushDialog";

    public CodePushDialog(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @ReactMethod
    public void showDialog(@Nullable final String title, @Nullable final String message,
                           @Nullable final String button1Text, @Nullable final String button2Text,
                           final Callback successCallback, final Callback errorCallback) {
        final Activity currentActivity = getCurrentActivity();

        if (currentActivity == null || currentActivity.isFinishing()) {
            getReactApplicationContext().addLifecycleEventListener(new LifecycleEventListener() {
                @Override
                public void onHostResume() {
                    Activity resumedActivity = getCurrentActivity();
                    if (resumedActivity != null) {
                        getReactApplicationContext().removeLifecycleEventListener(this);
                        showDialogInternal(title, message, button1Text, button2Text, successCallback, resumedActivity);
                    }
                }

                @Override public void onHostPause() {}
                @Override public void onHostDestroy() {}
            });
        } else {
            showDialogInternal(title, message, button1Text, button2Text, successCallback, currentActivity);
        }
    }

    private void showDialogInternal(@Nullable String title, @Nullable String message,
                                    @Nullable String button1Text, @Nullable String button2Text,
                                    final Callback successCallback, Activity activity) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);

            DialogInterface.OnClickListener clickListener = (dialog, which) -> {
                dialog.dismiss();
                try {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        successCallback.invoke(0);
                    } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        successCallback.invoke(1);
                    } else {
                        throw new CodePushUnknownException("Unknown button ID pressed.");
                    }
                } catch (Throwable t) {
                    CodePushUtils.log(t);
                }
            };

            if (title != null) builder.setTitle(title);
            if (message != null) builder.setMessage(message);
            if (button1Text != null) builder.setPositiveButton(button1Text, clickListener);
            if (button2Text != null) builder.setNegativeButton(button2Text, clickListener);

            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Throwable t) {
            CodePushUtils.log(t);
            if (successCallback != null) {
                successCallback.invoke(-1);
            }
        }
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }
}
