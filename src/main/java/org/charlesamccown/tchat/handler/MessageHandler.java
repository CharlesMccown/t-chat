package org.charlesamccown.tchat.handler;

import org.charlesamccown.tchat.model.Message;
import org.charlesamccown.tchat.repository.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class MessageHandler {
    private MessageRepository messageRepository;

    public MessageHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Mono<ServerResponse> getAllMessages(ServerRequest serverRequest) {
        Flux<Message> messages = messageRepository.findAll();

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(messages, Message.class);
    }

    public Mono<ServerResponse> getMessage(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Message> messageMono = messageRepository.findById(id);
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();

        return messageMono
                .flatMap(message ->
                        ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(fromObject(message))
                )
                .switchIfEmpty(notFoundMono);
    }

    public Mono<ServerResponse> saveMessage(ServerRequest serverRequest) {
        Mono<Message> messageMono = serverRequest.bodyToMono(Message.class);

        return messageMono
                .flatMap(message ->
                        ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(messageRepository.save(message), Message.class)
                );
    }

    public Mono<ServerResponse> updateMessage(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Message> updateMessageMono = serverRequest.bodyToMono(Message.class);
        Mono<Message> messageMono = messageRepository.findById(id);
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();

        return messageMono.zipWith(updateMessageMono,
                (updateMessage, message) ->
                new Message(message.getId(), updateMessage.getBody(), new Date())
        )
        .flatMap(message ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(messageRepository.save(message), Message.class))
        .switchIfEmpty(notFoundMono);
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        Mono<Message> messageMono = messageRepository.findById(id);
        Mono<ServerResponse> notFoundMono = ServerResponse.notFound().build();

        return messageMono
                .flatMap(message ->
                        ServerResponse.ok()
                            .build(messageRepository.delete(message))
                )
                .switchIfEmpty(notFoundMono);
    }

    public Mono<ServerResponse> deleteAllMessages(ServerRequest serverRequest) {

        return ServerResponse.ok()
                    .build(messageRepository.deleteAll());
    }

    public Mono<ServerResponse> getMessageEvents(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(messageRepository.findAll(), Message.class);
    }
}
