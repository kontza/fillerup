package org.kontza.fillerup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TriggerService {
    private static final Logger logger = LoggerFactory.getLogger(TriggerService.class);
    private static final String SAMPLE_IMAGE = "/sample-image.jpg";
    private static final String PUSH_URI = "http://localhost:9110/";

    public void triggerIt(Boolean dirty) throws IOException {
        Class cls = TriggerService.class;
        InputStream inputStream = cls.getResourceAsStream(SAMPLE_IMAGE);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        sendBuffer(bytes, dirty);
    }

    private void sendBuffer(byte[] bytesToSend, Boolean dirty) {
        logger.info(">>> Going to send {} bytes...", bytesToSend.length);
        DisposableResources disposables = DisposableResources.build();

        HttpClient httpClient = HttpClient
                .create(disposables.getConnectionProvider())
                .runOn(disposables.getLoopResources());

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        if (dirty) {
            webClient = WebClient.builder().build();
        }

        var result = webClient
                .post()
                .uri(PUSH_URI)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(bytesToSend.length)
                .bodyValue(bytesToSend)
                .exchange()
                .block()
                .bodyToMono(String.class)
                .block();
        logger.info(">>> Result = {}", result);
        if (!dirty) {
            disposables.getConnectionProvider().dispose();
            disposables.getLoopResources().dispose();
        }
    }
}
