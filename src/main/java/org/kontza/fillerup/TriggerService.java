package org.kontza.fillerup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    private static final String SAMPLE_IMAGE = "/sample-image.jpg";
    private static final String PUSH_URI = "http://localhost:7110";

    public void triggerIt() throws IOException {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        sendBuffer(bytes);
    }

    private void sendBuffer(byte[] bytesToSend) {
        WebClient client = WebClient.create();
        Mono<String> result = client
                .post()
                .uri(PUSH_URI)
                .body(Mono.just(bytesToSend), String.class)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    logger.error(">>> Failed to push: {}", response.statusCode());
                    return response.bodyToMono(String.class).switchIfEmpty(Mono.just("")).map(body -> {
                        logger.info(">>> Response body: {}", body);
                        return new RuntimeException("Failed!");
                    });
                })
                .bodyToMono(String.class);
        logger.error(">>> Result = {}", result.block());
    }
}
