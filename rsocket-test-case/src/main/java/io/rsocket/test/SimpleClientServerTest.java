/*
 * Copyright (C) 2019 SignalFx, Inc.
 */
package io.rsocket.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;


@SpringBootApplication
public class SimpleClientServerTest {

    public static void main(String[] args) {
        CloseableChannel server = RSocketFactory.receive()
                .acceptor((setup, rSocket) -> Mono.just(new AbstractRSocket() {
                    @Override
                    public Mono<Payload> requestResponse(Payload payload) {
                        return Mono.just(DefaultPayload.create("PONG"));
                    }
                }))
                .transport(TcpServerTransport.create("localhost", 8001))
                .start()
                .block();

        RSocket client = RSocketFactory.connect()
                .transport(TcpClientTransport.create("localhost", 8001))
                .start()
                .block();
        Mono<Payload> pong = client.requestResponse(DefaultPayload.create("PING"));
        System.out.println("\n\nReceived: " + pong.block());

    }
}
