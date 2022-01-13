package com.getaroom.app.repository.mongodb;

import com.getaroom.app.entity.mongodb.EventHistory;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventHistoryRepository extends MongoRepository<EventHistory, String> {}
