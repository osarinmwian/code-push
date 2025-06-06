package com.codepushsdk.react;

class CodePushInvalidPublicKeyException extends RuntimeException {

    public CodePushInvalidPublicKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodePushInvalidPublicKeyException(String message) {
        super(message);
    }
}