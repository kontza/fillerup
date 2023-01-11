package org.kontza.fillerup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    public static final String SAMPLE_IMAGE = "/sample-image.jpg";
    public static final String PUSH_URI = "http://localhost:9110/";

    public void triggerIt(Boolean defaultClient) {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            logger.error("Input stream operation failed! Cannot continue!");
            return;
        }
        String result;
        if (defaultClient) {
            logger.info("Sending {} bytes with default client...", bytes.length);
            result = defaultSendBuffer(bytes);
        } else {
            result = null;
            logger.warn("Not implemented!");
        }
        if (result.startsWith("Internal")) {
            logger.error("    Result = {}", result);
        } else {
            logger.info("    Result = {}", result);
        }
        logger.info("Done.");
    }

    private String defaultSendBuffer(byte[] bytesToSend) {
        try {
            InputStream bis = new ByteArrayInputStream(bytesToSend);
            WebClient defaultWebClient = WebClient.builder().build();
            var res = BodyInserters.fromResource(new InputStreamResource(bis));
            return defaultWebClient
                    .post()
                    .uri(PUSH_URI)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(res)
                    .retrieve()
                    .onStatus(Predicate.not(HttpStatus::is2xxSuccessful), clientResponse -> {
                        clientResponse.releaseBody();
                        return Mono.error(new RemoteServiceException("Send failed!", clientResponse.rawStatusCode()));
                    })
                    .bodyToMono(String.class)
                    .block();
        } catch (RemoteServiceException e) {
            logger.error("defaultSendBuffer failed:", e);
        }
        return "";
    }
}
