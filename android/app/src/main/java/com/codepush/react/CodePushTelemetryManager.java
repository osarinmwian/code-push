package com.codepushsdk.react;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import org.json.JSONException;
import org.json.JSONObject;

public class CodePushTelemetryManager {
    private final SharedPreferences settings;

    private static final String APP_VERSION_KEY = "appVersion";
    private static final String DEPLOYMENT_FAILED_STATUS = "DeploymentFailed";
    private static final String DEPLOYMENT_KEY_KEY = "deploymentKey";
    private static final String DEPLOYMENT_SUCCEEDED_STATUS = "DeploymentSucceeded";
    private static final String LABEL_KEY = "label";
    private static final String LAST_DEPLOYMENT_REPORT_KEY = "CODE_PUSH_LAST_DEPLOYMENT_REPORT";
    private static final String PACKAGE_KEY = "package";
    private static final String PREVIOUS_DEPLOYMENT_KEY_KEY = "previousDeploymentKey";
    private static final String PREVIOUS_LABEL_OR_APP_VERSION_KEY = "previousLabelOrAppVersion";
    private static final String RETRY_DEPLOYMENT_REPORT_KEY = "CODE_PUSH_RETRY_DEPLOYMENT_REPORT";
    private static final String STATUS_KEY = "status";

    public CodePushTelemetryManager(Context context) {
        this.settings = context.getSharedPreferences(CodePushConstants.CODE_PUSH_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Generates a report if the current binary version has changed.
     */
    public WritableMap getBinaryUpdateReport(String appVersion) {
        String prevIdentifier = getPreviousStatusReportIdentifier();

        if (prevIdentifier == null) {
            clearRetryStatusReport();
            WritableMap map = Arguments.createMap();
            map.putString(APP_VERSION_KEY, appVersion);
            return map;
        }

        if (!prevIdentifier.equals(appVersion)) {
            clearRetryStatusReport();
            WritableMap map = Arguments.createMap();
            map.putString(APP_VERSION_KEY, appVersion);

            if (isCodePushIdentifier(prevIdentifier)) {
                map.putString(PREVIOUS_DEPLOYMENT_KEY_KEY, extractDeploymentKey(prevIdentifier));
                map.putString(PREVIOUS_LABEL_OR_APP_VERSION_KEY, extractLabel(prevIdentifier));
            } else {
                map.putString(PREVIOUS_LABEL_OR_APP_VERSION_KEY, prevIdentifier);
            }

            return map;
        }

        return null;
    }

    /**
     * Retrieves any update status report saved for retrying.
     */
    public WritableMap getRetryStatusReport() {
        String json = settings.getString(RETRY_DEPLOYMENT_REPORT_KEY, null);
        if (json == null) return null;

        clearRetryStatusReport();
        try {
            JSONObject retryData = new JSONObject(json);
            return CodePushUtils.convertJsonObjectToWritable(retryData);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Constructs a rollback report for a failed update package.
     */
    public WritableMap getRollbackReport(WritableMap lastFailedPackage) {
        WritableMap report = Arguments.createMap();
        report.putMap(PACKAGE_KEY, lastFailedPackage);
        report.putString(STATUS_KEY, DEPLOYMENT_FAILED_STATUS);
        return report;
    }

    /**
     * Constructs an update report if the current package differs from the previous.
     */
    public WritableMap getUpdateReport(WritableMap currentPackage) {
        String currentId = getStatusIdentifier(currentPackage);
        String prevId = getPreviousStatusReportIdentifier();

        if (currentId == null || currentId.equals(prevId)) return null;

        clearRetryStatusReport();
        WritableMap report = Arguments.createMap();
        report.putMap(PACKAGE_KEY, currentPackage);
        report.putString(STATUS_KEY, DEPLOYMENT_SUCCEEDED_STATUS);

        if (prevId != null) {
            if (isCodePushIdentifier(prevId)) {
                report.putString(PREVIOUS_DEPLOYMENT_KEY_KEY, extractDeploymentKey(prevId));
                report.putString(PREVIOUS_LABEL_OR_APP_VERSION_KEY, extractLabel(prevId));
            } else {
                report.putString(PREVIOUS_LABEL_OR_APP_VERSION_KEY, prevId);
            }
        }

        return report;
    }

    /**
     * Records the provided status report as the last successful report.
     */
    public void recordStatusReported(ReadableMap statusReport) {
        if (DEPLOYMENT_FAILED_STATUS.equals(CodePushUtils.tryGetString(statusReport, STATUS_KEY))) return;

        if (statusReport.hasKey(APP_VERSION_KEY)) {
            saveStatusIdentifier(statusReport.getString(APP_VERSION_KEY));
        } else if (statusReport.hasKey(PACKAGE_KEY)) {
            ReadableMap pkg = statusReport.getMap(PACKAGE_KEY);
            String identifier = getStatusIdentifier(pkg);
            if (identifier != null) {
                saveStatusIdentifier(identifier);
            }
        }
    }

    /**
     * Persists the report for a retry.
     */
    public void saveStatusReportForRetry(ReadableMap statusReport) {
        JSONObject json = CodePushUtils.convertReadableToJsonObject(statusReport);
        settings.edit().putString(RETRY_DEPLOYMENT_REPORT_KEY, json.toString()).apply();
    }

    // Private helpers

    private void clearRetryStatusReport() {
        settings.edit().remove(RETRY_DEPLOYMENT_REPORT_KEY).apply();
    }

    private String getPreviousStatusReportIdentifier() {
        return settings.getString(LAST_DEPLOYMENT_REPORT_KEY, null);
    }

    private void saveStatusIdentifier(String identifier) {
        settings.edit().putString(LAST_DEPLOYMENT_REPORT_KEY, identifier).apply();
    }

    private String getStatusIdentifier(ReadableMap pkg) {
        String deploymentKey = CodePushUtils.tryGetString(pkg, DEPLOYMENT_KEY_KEY);
        String label = CodePushUtils.tryGetString(pkg, LABEL_KEY);
        return (deploymentKey != null && label != null) ? deploymentKey + ":" + label : null;
    }

    private boolean isCodePushIdentifier(String identifier) {
        return identifier != null && identifier.contains(":");
    }

    private String extractDeploymentKey(String identifier) {
        String[] parts = identifier.split(":");
        return parts.length > 0 ? parts[0] : null;
    }

    private String extractLabel(String identifier) {
        String[] parts = identifier.split(":");
        return parts.length > 1 ? parts[1] : null;
    }
}
