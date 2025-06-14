package com.codepushsdk.react;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class DownloadProgress {
    private final long totalBytes;
    private final long receivedBytes;

    public DownloadProgress(long totalBytes, long receivedBytes) {
        this.totalBytes = totalBytes;
        this.receivedBytes = receivedBytes;
    }

    public WritableMap toWritableMap() {
        WritableMap map = new WritableNativeMap();
        if (totalBytes <= Integer.MAX_VALUE && receivedBytes <= Integer.MAX_VALUE) {
            map.putInt("totalBytes", (int) totalBytes);
            map.putInt("receivedBytes", (int) receivedBytes);
        } else {
            map.putDouble("totalBytes", totalBytes);
            map.putDouble("receivedBytes", receivedBytes);
        }
        return map;
    }

    public boolean isCompleted() {
        return totalBytes > 0 && receivedBytes >= totalBytes;
    }
}
