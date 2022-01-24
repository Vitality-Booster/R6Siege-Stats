package com.example.r6siegestats.r6sstatsapi.external.objects.error;

public class UbiHardApiException extends UbiApiException {
    private final Throwable rootException;

    public UbiHardApiException(String message, Throwable rootException) {
        super("Hard API Exception: " + message);
        this.rootException = rootException;
    }

    public UbiHardApiException(String message) {
        this("Hard API Exception: " + message, null);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }


    public Throwable getRootException() {
        return rootException;
    }
}
