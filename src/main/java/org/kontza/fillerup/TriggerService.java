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
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    private static final String SAMPLE_IMAGE = "/sample-image.jpg";
    private static final String PUSH_URI = "http://localhost:7110/";

    public void triggerIt(boolean doCleanUp) throws IOException {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        sendBuffer(bytes, doCleanUp);
    }

    private void sendBuffer(byte[] bytesToSend, boolean doCleanUp) {
        logger.info(">>> Going to send {} bytes...", bytesToSend.length);
        LoopResources resources = LoopResources.create("test-loop");
        ConnectionProvider provider = ConnectionProvider.builder("test-pool").build();

        TcpClient tcpClient = TcpClient.create(provider).runOn(resources, false);
        HttpClient httpClient = HttpClient.from(tcpClient);

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
        provider.dispose();
        resources.dispose();
    }
}
