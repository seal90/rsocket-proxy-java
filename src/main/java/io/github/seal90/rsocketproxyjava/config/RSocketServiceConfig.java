package io.github.seal90.rsocketproxyjava.config;

import io.github.seal90.rsocketproxyjava.integration.RedisOperator;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class RSocketServiceConfig {

    @Autowired
    private RedisOperator redisOperator;

    @Bean
    public RSocket serviceRSocket() {
        return new RSocket() {

            @Override
            public Mono<Payload> requestResponse(Payload payload) {

                Mono<Payload> setResponse = redisOperator.stringSet("hello", "world");

                Mono<Payload> getResponse  = redisOperator.stringGet("hello");

//                Payload responsePayload = DefaultPayload.create(value.getBytes(StandardCharsets.UTF_8));
//                rSocketClient.requestResponse()
                return Mono.empty().then(setResponse).then(getResponse);
            }
        };
    }
}
