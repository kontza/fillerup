package org.kontza.fillerup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoteServiceException extends RuntimeException {
    private int statusCode;

    public RemoteServiceException(String message, int statusCode) {
        super(message);
        statusCode = statusCode;
    }
}
