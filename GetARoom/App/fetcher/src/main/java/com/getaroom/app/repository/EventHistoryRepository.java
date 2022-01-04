package com.getaroom.app.repository;

import com.getaroom.app.entity.EventHistory;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventHistoryRepository extends MongoRepository<EventHistory, String> {}
