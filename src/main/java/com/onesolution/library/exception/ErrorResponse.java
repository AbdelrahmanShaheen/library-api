package com.onesolution.library.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
public class ErrorResponse {
    private String timestamp;
    private int status;
    private Map<String,String> messages;

    public ErrorResponse(int status , Map<String,String> messages) {
        this.timestamp = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
        this.status = status;
        this.messages = messages;
    }
}
