package org.charlesamccown.tchat;

import org.charlesamccown.tchat.handler.MessageHandler;
import org.charlesamccown.tchat.model.Message;
import org.charlesamccown.tchat.repository.MessageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Date;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

@SpringBootApplication
public class TChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(TChatApplication.class, args);
	}

	@Bean
	CommandLineRunner init(MessageRepository messageRepository) {
		return args -> {
			Flux<Message> messageFlux = Flux.just(
					new Message(null, "Welcome to TChat", new Date()))
					.flatMap(messageRepository::save);

			messageFlux
					.thenMany(messageRepository.findAll())
					.subscribe(System.out::println);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes(MessageHandler messageHandler) {
		return nest(path("/messages"),
				nest(accept(MediaType.APPLICATION_JSON).or(contentType(MediaType.APPLICATION_JSON)).or(accept(MediaType.TEXT_EVENT_STREAM)),
						route(GET("/"), messageHandler::getAllMessages)
						.andRoute(method(HttpMethod.POST), messageHandler::saveMessage)
						.andRoute(GET("/events"), messageHandler::getMessageEvents)
						.andRoute(DELETE("/"), messageHandler::deleteAllMessages)
						.andNest(path("/{id}"),
							route(method(HttpMethod.GET), messageHandler::getMessage)
								.andRoute(method(HttpMethod.PUT), messageHandler::updateMessage)
								.andRoute(method(HttpMethod.DELETE), messageHandler::deleteMessage)
				)
			)
		);
	}
}
