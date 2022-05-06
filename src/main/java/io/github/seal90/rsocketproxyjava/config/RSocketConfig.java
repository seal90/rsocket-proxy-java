package io.github.seal90.rsocketproxyjava.config;

import io.netty.buffer.*;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.unix.DomainSocketAddress;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.DefaultConnectionSetupPayload;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.SetupFrameCodec;
import io.rsocket.metadata.CompositeMetadata;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.nio.charset.StandardCharsets;

@Configuration
public class RSocketConfig {

    @Autowired
    private RSocket serviceRSocket;

    private final String serviceName = "hello_world_001";

    private final String local_listen = "/tmp/proxy.socket";

    @Bean
    public RSocket rSocketClient() {

        TcpClient tcpClient = TcpClient.create().remoteAddress(() -> new DomainSocketAddress(local_listen));

        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        CompositeByteBuf compositeMetaData = allocator.compositeBuffer();
        String serviceNameMimeType = "X-FROM-SERVICE-NAME";
        ByteBuf serviceNameMetadata = ByteBufAllocator.DEFAULT.heapBuffer(10);
        serviceNameMetadata.writeBytes(serviceName.getBytes(StandardCharsets.UTF_8));
        CompositeMetadataCodec.encodeAndAddMetadata(compositeMetaData, allocator, serviceNameMimeType, serviceNameMetadata);

        ByteBuf data = ByteBufAllocator.DEFAULT.heapBuffer(10);
        Payload defaultPayload = DefaultPayload.create(data, compositeMetaData.slice());

        RSocket clientRSocket =
                RSocketConnector.create()
                        .metadataMimeType(WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.getString())
                        .setupPayload(defaultPayload)
                        .acceptor((setup, sendingRSocket) -> Mono.just(serviceRSocket))
                        .connect(TcpClientTransport.create(tcpClient))
                        .block();
        clientRSocket.requestResponse(DefaultPayload.create(""));
        return clientRSocket;
    }



}
