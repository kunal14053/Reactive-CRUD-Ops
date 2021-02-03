package com.reactive.application.goreactive.exception;

import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.ServerRequest;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class FunctionalErrorWebExceptionHandler {/*extends AbstractErrorWebExceptionHandler {

    public FunctionalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                              ServerCodecConfigurer serverCodecConfigurer,
                                              ApplicationContext applicationContext) {

        super(errorAttributes, new ResourceProperties() , applicationContext);
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::rednerErrorResponse);
    }

    private Mono<ServerResponse> rednerErrorResponse(ServerRequest serverRequest){

        Map<String, Object> errorAttributeMap =  getErrorAttributes(serverRequest, false);

        log.info("errorAttributesMap: "+errorAttributeMap);

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(errorAttributeMap.get("message")));

    }
*/
}