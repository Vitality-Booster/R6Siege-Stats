package com.example.r6siegestats.r6sstatsapi.external.objects.error;

public class UbiApiErrorResponseException extends UbiApiException {
    private String errorResponse;

    public UbiApiErrorResponseException(String errorResponse, String message) {
        super(message);
        this.errorResponse = errorResponse;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String getErrorResponse() {
        return errorResponse;
    }
}
