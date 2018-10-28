package org.charlesamccown.tchat.repository;

import org.charlesamccown.tchat.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository
        extends ReactiveMongoRepository<Message, String> {
}
