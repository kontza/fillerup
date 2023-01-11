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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    public static final String SAMPLE_IMAGE = "/sample-image.jpg";
    public static final String PUSH_URI = "http://localhost:9110/";
    private WebClient webClient;

    public TriggerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public void triggerIt(Boolean defaultClient) throws IOException {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        inputStream.close();
        String result;
        if (defaultClient) {
            logger.info("Sending {} bytes with default client...", bytes.length);
            result = defaultSendBuffer(bytes);
        } else {
            result = customSendBuffer(bytes);
        }
        if (result.startsWith("Internal")) {
            logger.error("    Result = {}", result);
        } else {
            logger.info("    Result = {}", result);
        }
        logger.info("Done.");
    }

    private String defaultSendBuffer(byte[] bytesToSend) {
        WebClient defaultWebClient = WebClient.builder().build();
        return defaultWebClient
                .post()
                .uri(PUSH_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytesToSend.length)
                .bodyValue(bytesToSend)
                .retrieve()
                .onStatus(Predicate.not(HttpStatus::is2xxSuccessful), clientResponse -> Mono.error(new RemoteServiceException("Failed miserably!")))
                .bodyToMono(String.class)
                .onErrorMap(throwable -> {
                    logger.error(">>> BARF:", throwable.getCause().getCause());
                    return new RemoteServiceException("BARF!");
                })
                .onErrorReturn("FAIL!!!")
                .block();
    }

    private String customSendBuffer(byte[] bytesToSend) {
        return webClient
                .post()
                .uri(PUSH_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytesToSend.length)
                .bodyValue(bytesToSend)
                .retrieve()
                .onStatus(Predicate.not(HttpStatus::is2xxSuccessful), clientResponse -> Mono.error(new RemoteServiceException("Failed miserably!")))
                .bodyToMono(String.class)
                .block();
    }
}
