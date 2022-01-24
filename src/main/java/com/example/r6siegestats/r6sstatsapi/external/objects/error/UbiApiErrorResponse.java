package com.example.r6siegestats.r6sstatsapi.external.objects.error;

import lombok.Data;

import java.util.Date;

@Data
public class UbiApiErrorResponse {
    // This is how one of these error objects usually looks like
    // "errorCode":1003,
    // "message": "Resource 'https://public-ubiservices.ubi.com/v3/users/abc' not found.",
    // "httpCode":404,
    // "errorContext":"UbiServices.Gateway",
    // "moreInfo":"A link to more information will be coming soon. Please contact UbiServices for more support.",
    // "transactionTime":"2020-11-22T21:03:57Z",
    // "transactionId":"d526ddfc-ed2d-4824-8d9d-18c94db8d8ba"

    private Integer errorCode;
    private String message;
    private Integer httpCode;
    private String errorContext;
    private String moreInfo;
    private Date transactionTime;
    private String transactionId;

}
