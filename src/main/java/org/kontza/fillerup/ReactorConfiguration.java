package org.kontza.fillerup;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.LoopResources;

@Configuration
public class ReactorConfiguration {

    private static final int THREAD_COUNT = 20;

    @Bean
    public NioEventLoopGroup nioEventLoopGroup() {
        return new NioEventLoopGroup(THREAD_COUNT);
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_COUNT);
        executor.setMaxPoolSize(THREAD_COUNT);
        executor.setThreadNamePrefix(getClass().getCanonicalName());
        executor.initialize();
        return executor;
    }

    @Bean
    public ReactorResourceFactory reactorResourceFactory(NioEventLoopGroup eventLoopGroup) {
        ReactorResourceFactory f = new ReactorResourceFactory();
        f.setLoopResources(b -> eventLoopGroup);
        f.setUseGlobalResources(false);
        return f;
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory r) {
        return new ReactorClientHttpConnector(r, m -> m);
    }

    @Bean
    public WebClient webClient(ReactorClientHttpConnector r) {
        return WebClient.builder().baseUrl(TriggerService.PUSH_URI).clientConnector(r).build();
    }
}
