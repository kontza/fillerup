package org.kontza.fillerup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    private static final String SAMPLE_IMAGE = "/sample-image.jpg";
    private static final String PUSH_URI = "http://localhost:9110/";

    public void triggerIt() throws IOException {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        sendBuffer(bytes);
    }

    private void sendBuffer(byte[] bytesToSend) {
        logger.info(">>> Going to send {} bytes...", bytesToSend.length);
        DisposableResources disposables = DisposableResources.build();

        HttpClient httpClient = HttpClient
                .create(disposables.getConnectionProvider())
                .runOn(disposables.getLoopResources());

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        String result = webClient
                .post()
                .uri(PUSH_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(bytesToSend)
                .retrieve()
                .onStatus(HttpStatus::isError, response -> {
                    logger.error(">>> Failed to push: {}", response.statusCode());
                    return response.bodyToMono(String.class).switchIfEmpty(Mono.just("")).map(body -> {
                        logger.info(">>> Response body: {}", body);
                        return new RuntimeException("Failed!");
                    });
                })
                .bodyToMono(String.class).block();
        logger.info(">>> Result = {}", result);
        disposables.getConnectionProvider().dispose();
        disposables.getLoopResources().dispose();
    }
}
