package com.ewallet.userservice.configurations.feignConfig;

import com.ewallet.userservice.exceptions.DuplicateEntryException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder=new Default();
    @Override
    public Exception decode(String s, Response response) {
        HttpStatus httpStatus= HttpStatus.valueOf(response.status());

        return switch (httpStatus) {
            case CONFLICT -> new DuplicateEntryException("Duplicate entry "+s);
            default -> errorDecoder.decode(s, response);
        };
    }

}
