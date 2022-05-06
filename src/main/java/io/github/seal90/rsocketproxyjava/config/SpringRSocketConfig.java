package io.github.seal90.rsocketproxyjava.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

//@Configuration
public class SpringRSocketConfig {

    @Autowired
    private RSocketRequester.Builder rsocketRequesterBuilder;

    @Bean
    public RSocketRequester rsocketRequester() {
        RSocketRequester rsocketRequester = rsocketRequesterBuilder
//                .setupRoute("shell-client")
//                .setupData(CLIENT_ID)
//                .setupMetadata(user, SIMPLE_AUTH)
//                .rsocketStrategies(builder ->
//                        builder.encoder(new SimpleAuthenticationEncoder()))
//                .rsocketConnector(connector -> connector.acceptor(responder))
                .connectTcp("localhost", 7979)
//                .connect(TcpClientTransport.create(TcpClient.create().remoteAddress(()-> new DomainSocketAddress("/tmp/proxy.socket"))))
                .block();
        return rsocketRequester;
    }
}
