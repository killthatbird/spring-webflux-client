package com.reactiveclient.client.codec;

import com.reactiveclient.ErrorDecoder;
import com.reactiveclient.client.DataBuffers;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

class HttpServerErrorDecoder implements ErrorDecoder<HttpServerErrorException> {
    @Override
    public boolean canDecode(HttpStatus httpStatus) {
        return httpStatus.is5xxServerError();
    }

    @Override
    public HttpServerErrorException decode(HttpStatus httpStatus, DataBuffer inputMessage) {
        return new HttpServerErrorException(httpStatus, DataBuffers.readToString(inputMessage));
    }
}
