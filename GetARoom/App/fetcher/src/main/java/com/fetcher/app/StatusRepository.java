package com.fetcher.app;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StatusRepository extends MongoRepository<Status, String> {

    Optional<Status> findByRoom(String room);
}
