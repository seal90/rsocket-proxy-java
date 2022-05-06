package io.github.seal90.rsocketproxyjava.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.metadata.CompositeMetadataCodec;
import io.rsocket.util.DefaultPayload;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class RedisOperator {


    private String redisServiceName = "redis_service_0001";

    @Lazy
    @Autowired
    private RSocket rSocketClient;

    public Mono<Payload> stringSet(String key, String value) {

        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        CompositeByteBuf compositeMetaData = allocator.compositeBuffer();
        String serviceNameMimeType = "X-TARGET-SERVICE-NAME";
        ByteBuf serviceNameMetadata = ByteBufAllocator.DEFAULT.heapBuffer(10);
        serviceNameMetadata.writeBytes(redisServiceName.getBytes(StandardCharsets.UTF_8));
        CompositeMetadataCodec.encodeAndAddMetadata(compositeMetaData, allocator, serviceNameMimeType, serviceNameMetadata);

        RedisOperatorEntity operatorEntity = new RedisOperatorEntity();
        operatorEntity.setKey(key);
        operatorEntity.setValue(value.getBytes(StandardCharsets.UTF_8));
        operatorEntity.setOperator("STRING_SET");

        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cborFactory);
        try {
            byte[] dataBytes = mapper.writeValueAsBytes(operatorEntity);
            ByteBuf data = ByteBufAllocator.DEFAULT.heapBuffer(10);
            data.writeBytes(dataBytes);

            Payload requestPayload = DefaultPayload.create(data, compositeMetaData.slice());

            Mono<Payload> responsePayload = rSocketClient.requestResponse(requestPayload);

            return responsePayload;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Mono.empty();

    }

    public Mono<Payload> stringGet(String key) {

        ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
        CompositeByteBuf compositeMetaData = allocator.compositeBuffer();
        String serviceNameMimeType = "X-TARGET-SERVICE-NAME";
        ByteBuf serviceNameMetadata = ByteBufAllocator.DEFAULT.heapBuffer(10);
        serviceNameMetadata.writeBytes(redisServiceName.getBytes(StandardCharsets.UTF_8));
        CompositeMetadataCodec.encodeAndAddMetadata(compositeMetaData, allocator, serviceNameMimeType, serviceNameMetadata);

        RedisOperatorEntity operatorEntity = new RedisOperatorEntity();
        operatorEntity.setKey(key);
        operatorEntity.setOperator("STRING_GET");

        CBORFactory cborFactory = new CBORFactory();
        ObjectMapper mapper = new ObjectMapper(cborFactory);
        try {
            byte[] dataBytes = mapper.writeValueAsBytes(operatorEntity);
            ByteBuf data = ByteBufAllocator.DEFAULT.heapBuffer(10);
            data.writeBytes(dataBytes);
            System.out.println(dataBytes);

            Payload requestPayload = DefaultPayload.create(data, compositeMetaData.slice());

            Mono<Payload> responsePayload = rSocketClient.requestResponse(requestPayload);

//            String val = responsePayload.getDataUtf8();
//            return val;
            return responsePayload;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Mono.empty();

    }

    @Data
    public static class RedisOperatorEntity {

        private String key;

        private byte[] value;

        private String operator;
    }
}
