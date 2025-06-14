package com.codepushsdk.react;

import android.content.Context;
import android.util.Base64;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class CodePushUpdateUtils {

    public static final String NEW_LINE = System.getProperty("line.separator");

    private static final String __MACOSX = "__MACOSX/";
    private static final String DS_STORE = ".DS_Store";
    private static final String CODEPUSH_METADATA = ".codepushrelease";

    public static boolean isHashIgnored(String relativeFilePath) {
        return relativeFilePath.startsWith(__MACOSX)
                || relativeFilePath.equals(DS_STORE)
                || relativeFilePath.endsWith("/" + DS_STORE)
                || relativeFilePath.equals(CODEPUSH_METADATA)
                || relativeFilePath.endsWith("/" + CODEPUSH_METADATA);
    }

    private static void addContentsOfFolderToManifest(String folderPath, String pathPrefix, List<String> manifest) {
        File folder = new File(folderPath);
        File[] folderFiles = folder.listFiles();

        if (folderFiles == null) return;

        for (File file : folderFiles) {
            String fileName = file.getName();
            String fullFilePath = file.getAbsolutePath();
            String relativePath = pathPrefix.isEmpty() ? fileName : pathPrefix + "/" + fileName;

            if (isHashIgnored(relativePath)) continue;

            if (file.isDirectory()) {
                addContentsOfFolderToManifest(fullFilePath, relativePath, manifest);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    manifest.add(relativePath + ":" + computeHash(fis));
                } catch (IOException e) {
                    throw new CodePushUnknownException("Unable to compute hash of update contents.", e);
                }
            }
        }
    }

    private static String computeHash(InputStream dataStream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream dis = new DigestInputStream(dataStream, digest)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1);
            }
            byte[] hash = digest.digest();
            return String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            throw new CodePushUnknownException("SHA-256 algorithm not found.", e);
        }
    }

    public static void copyNecessaryFilesFromCurrentPackage(String diffManifestPath, String currentPath, String newPath) throws IOException {
        if (currentPath == null || !new File(currentPath).exists()) {
            CodePushUtils.log("Current package path is invalid. Skipping diff copy.");
            return;
        }

        FileUtils.copyDirectoryContents(currentPath, newPath);

        try {
            JSONObject diffManifest = CodePushUtils.getJsonObjectFromFile(diffManifestPath);
            JSONArray deletedFiles = diffManifest.getJSONArray("deletedFiles");
            for (int i = 0; i < deletedFiles.length(); i++) {
                File file = new File(newPath, deletedFiles.getString(i));
                if (file.exists()) file.delete();
            }
        } catch (JSONException e) {
            throw new CodePushUnknownException("Failed to apply diff manifest.", e);
        }
    }

    public static String findJSBundleInUpdateContents(String folderPath, String expectedFileName) {
        File folder = new File(folderPath);
        File[] folderFiles = folder.listFiles();

        if (folderFiles == null) return null;

        for (File file : folderFiles) {
            String fullPath = CodePushUtils.appendPathComponent(folderPath, file.getName());

            if (file.isDirectory()) {
                String subPath = findJSBundleInUpdateContents(fullPath, expectedFileName);
                if (subPath != null) {
                    return CodePushUtils.appendPathComponent(file.getName(), subPath);
                }
            } else if (file.getName().equals(expectedFileName)) {
                return file.getName();
            }
        }

        return null;
    }

    public static String getHashForBinaryContents(Context context, boolean isDebugMode) {
        try {
            return CodePushUtils.getStringFromInputStream(
                    context.getAssets().open(CodePushConstants.CODE_PUSH_HASH_FILE_NAME));
        } catch (IOException e) {
            try {
                return CodePushUtils.getStringFromInputStream(
                        context.getAssets().open(CodePushConstants.CODE_PUSH_OLD_HASH_FILE_NAME));
            } catch (IOException ex) {
                if (!isDebugMode) {
                    CodePushUtils.log("Failed to read binary hash. Ensure 'codepush.gradle' is configured.");
                }
                return null;
            }
        }
    }

    public static void verifyFolderHash(String folderPath, String expectedHash) {
        CodePushUtils.log("Verifying hash for folder: " + folderPath);

        List<String> manifest = new ArrayList<>();
        addContentsOfFolderToManifest(folderPath, "", manifest);
        Collections.sort(manifest);

        JSONArray manifestJson = new JSONArray(manifest);
        String manifestStr = manifestJson.toString().replace("\\/", "/");

        CodePushUtils.log("Generated manifest: " + manifestStr);

        String actualHash = computeHash(new ByteArrayInputStream(manifestStr.getBytes()));
        CodePushUtils.log("Expected hash: " + expectedHash + " | Actual hash: " + actualHash);

        if (!expectedHash.equals(actualHash)) {
            throw new CodePushInvalidUpdateException("Folder hash mismatch. Data integrity check failed.");
        }

        CodePushUtils.log("Folder hash verification succeeded.");
    }

    public static PublicKey parsePublicKey(String keyString) {
        try {
            keyString = keyString
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace(NEW_LINE, "")
                    .trim();

            byte[] decoded = Base64.decode(keyString.getBytes(), Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            CodePushUtils.log("Failed to parse public key: " + e.getMessage());
            return null;
        }
    }

    public static Map<String, Object> verifyAndDecodeJWT(String jwt, PublicKey publicKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
            if (signedJWT.verify(verifier)) {
                Map<String, Object> claims = signedJWT.getJWTClaimsSet().getClaims();
                CodePushUtils.log("JWT verified. Claims: " + claims);
                return claims;
            }
        } catch (Exception e) {
            CodePushUtils.log("JWT verification failed: " + e.getMessage());
        }

        return null;
    }

    public static String getSignatureFilePath(String folderPath) {
        return CodePushUtils.appendPathComponent(
                CodePushUtils.appendPathComponent(folderPath, CodePushConstants.CODE_PUSH_FOLDER_PREFIX),
                CodePushConstants.BUNDLE_JWT_FILE
        );
    }

    public static String getSignature(String folderPath) {
        try {
            return FileUtils.readFileToString(getSignatureFilePath(folderPath));
        } catch (IOException e) {
            CodePushUtils.log("Failed to read signature: " + e.getMessage());
            return null;
        }
    }

    public static void verifyUpdateSignature(String folderPath, String packageHash, String publicKeyString) {
        CodePushUtils.log("Verifying update signature in: " + folderPath);

        PublicKey publicKey = parsePublicKey(publicKeyString);
        if (publicKey == null) {
            throw new CodePushInvalidUpdateException("Public key is invalid or missing.");
        }

        String signature = getSignature(folderPath);
        if (signature == null) {
            throw new CodePushInvalidUpdateException("Update signature file not found.");
        }

        Map<String, Object> claims = verifyAndDecodeJWT(signature, publicKey);
        if (claims == null) {
            throw new CodePushInvalidUpdateException("JWT signature is not valid.");
        }

        String contentHash = (String) claims.get("contentHash");
        if (contentHash == null || !contentHash.equals(packageHash)) {
            throw new CodePushInvalidUpdateException("Signature content hash mismatch.");
        }

        CodePushUtils.log("Update signature verified successfully.");
    }
}
