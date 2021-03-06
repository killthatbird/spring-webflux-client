package com.webfluxclient.client;

import com.webfluxclient.codec.HttpErrorReader;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ClientHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

class ErrorExtractors {

    static <T> ErrorExtractor<Mono<T>, ClientHttpResponse> toMono() {
        return (inputMessage, context) -> readWithMessageReaders(
                inputMessage.getStatusCode(),
                context,
                httpExceptionReader -> httpExceptionReader.readMono(inputMessage));
    }

    static <T> ErrorExtractor<Flux<T>, ClientHttpResponse> toFlux() {
        return (inputMessage, context) -> readWithMessageReaders(inputMessage.getStatusCode(),
                context,
                httpExceptionReader -> httpExceptionReader.read(inputMessage));
    }

    private static <T, S extends Publisher<T>> S readWithMessageReaders(
            HttpStatus httpStatus,
            ErrorExtractor.Context context,
            Function<HttpErrorReader, S> readerFunction) {

        HttpErrorReader httpErrorReader = context.exceptionReaders()
                .stream()
                .filter(r -> r.canRead(httpStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No HttpErrorReader available for status: " + httpStatus.value()));

        return readerFunction.apply(httpErrorReader);
    }
}
