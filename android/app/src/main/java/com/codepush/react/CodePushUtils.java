package com.codepushsdk.react;

import android.util.Log;

import com.facebook.react.bridge.*;

import org.json.*;

import java.io.*;
import java.util.Iterator;

public class CodePushUtils {

    public static String appendPathComponent(String basePath, String appendPathComponent) {
        return new File(basePath, appendPathComponent).getAbsolutePath();
    }

    public static WritableArray convertJsonArrayToWritable(JSONArray jsonArr) {
        WritableArray array = Arguments.createArray();

        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                Object obj = jsonArr.get(i);

                if (obj instanceof JSONObject)
                    array.pushMap(convertJsonObjectToWritable((JSONObject) obj));
                else if (obj instanceof JSONArray)
                    array.pushArray(convertJsonArrayToWritable((JSONArray) obj));
                else if (obj instanceof String)
                    array.pushString((String) obj);
                else if (obj instanceof Double)
                    array.pushDouble((Double) obj);
                else if (obj instanceof Integer)
                    array.pushInt((Integer) obj);
                else if (obj instanceof Boolean)
                    array.pushBoolean((Boolean) obj);
                else if (obj == null)
                    array.pushNull();
                else
                    throw new CodePushUnknownException("Unsupported value in JSONArray: " + obj);

            } catch (JSONException e) {
                throw new CodePushUnknownException("Invalid JSON at index " + i + ": " + jsonArr, e);
            }
        }

        return array;
    }

    public static WritableMap convertJsonObjectToWritable(JSONObject jsonObj) {
        WritableMap map = Arguments.createMap();
        Iterator<String> keys = jsonObj.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object value = jsonObj.isNull(key) ? null : jsonObj.get(key);

                if (value instanceof JSONObject)
                    map.putMap(key, convertJsonObjectToWritable((JSONObject) value));
                else if (value instanceof JSONArray)
                    map.putArray(key, convertJsonArrayToWritable((JSONArray) value));
                else if (value instanceof String)
                    map.putString(key, (String) value);
                else if (value instanceof Double)
                    map.putDouble(key, (Double) value);
                else if (value instanceof Long)
                    map.putDouble(key, ((Long) value).doubleValue());
                else if (value instanceof Integer)
                    map.putInt(key, (Integer) value);
                else if (value instanceof Boolean)
                    map.putBoolean(key, (Boolean) value);
                else if (value == null)
                    map.putNull(key);
                else
                    throw new CodePushUnknownException("Unsupported value in JSONObject: " + value);

            } catch (JSONException e) {
                throw new CodePushUnknownException("Failed to parse key: " + key + " in JSONObject: " + jsonObj, e);
            }
        }

        return map;
    }

    public static JSONArray convertReadableToJsonArray(ReadableArray array) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < array.size(); i++) {
            ReadableType type = array.getType(i);
            switch (type) {
                case Map:
                    jsonArray.put(convertReadableToJsonObject(array.getMap(i)));
                    break;
                case Array:
                    jsonArray.put(convertReadableToJsonArray(array.getArray(i)));
                    break;
                case String:
                    jsonArray.put(array.getString(i));
                    break;
                case Number:
                    double number = array.getDouble(i);
                    jsonArray.put((number == Math.floor(number)) && !Double.isInfinite(number)
                            ? (long) number
                            : number);
                    break;
                case Boolean:
                    jsonArray.put(array.getBoolean(i));
                    break;
                case Null:
                    jsonArray.put(JSONObject.NULL);
                    break;
                default:
                    throw new CodePushUnknownException("Unsupported ReadableType in array: " + type);
            }
        }

        return jsonArray;
    }

    public static JSONObject convertReadableToJsonObject(ReadableMap map) {
        JSONObject jsonObject = new JSONObject();
        ReadableMapKeySetIterator iterator = map.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = map.getType(key);

            try {
                switch (type) {
                    case Map:
                        jsonObject.put(key, convertReadableToJsonObject(map.getMap(key)));
                        break;
                    case Array:
                        jsonObject.put(key, convertReadableToJsonArray(map.getArray(key)));
                        break;
                    case String:
                        jsonObject.put(key, map.getString(key));
                        break;
                    case Number:
                        jsonObject.put(key, map.getDouble(key));
                        break;
                    case Boolean:
                        jsonObject.put(key, map.getBoolean(key));
                        break;
                    case Null:
                        jsonObject.put(key, JSONObject.NULL);
                        break;
                    default:
                        throw new CodePushUnknownException("Unsupported ReadableType in map: " + type);
                }
            } catch (JSONException e) {
                throw new CodePushUnknownException("Failed to insert key: " + key + " into JSONObject", e);
            }
        }

        return jsonObject;
    }

    public static String getStringFromInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }

            return buffer.toString().trim();
        }
    }

    public static JSONObject getJsonObjectFromFile(String filePath) throws IOException {
        String content = FileUtils.readFileToString(filePath);
        try {
            return new JSONObject(content);
        } catch (JSONException e) {
            throw new CodePushMalformedDataException("Invalid JSON in file: " + filePath, e);
        }
    }

    public static void writeJsonToFile(JSONObject json, String filePath) throws IOException {
        FileUtils.writeStringToFile(json.toString(), filePath);
    }

    public static void setJSONValueForKey(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new CodePushUnknownException("Failed to insert key: " + key + " with value: " + value, e);
        }
    }

    public static String tryGetString(ReadableMap map, String key) {
        try {
            return map.getString(key);
        } catch (NoSuchKeyException e) {
            return null;
        }
    }

    public static void log(String message) {
        Log.d(CodePushConstants.REACT_NATIVE_LOG_TAG, "[CodePush] " + message);
    }

    public static void log(Throwable tr) {
        Log.e(CodePushConstants.REACT_NATIVE_LOG_TAG, "[CodePush] Exception", tr);
    }

    public static void logBundleUrl(String path) {
        log("Loading JS bundle from: " + path);
    }
}
