package org.kontza.fillerup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.util.UUID;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class DisposableResources {
    LoopResources loopResources;
    ConnectionProvider connectionProvider;

    public static DisposableResources build() {
        DisposableResources disposableResources = new DisposableResources();
        String resourceName = UUID.randomUUID().toString();
        disposableResources.setLoopResources(LoopResources.create(resourceName));
        disposableResources.setConnectionProvider(ConnectionProvider.builder(resourceName).build());
        return disposableResources;
    }
}
