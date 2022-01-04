package com.getaroom.app.repository;

import com.getaroom.app.entity.StatusHistory;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatusHistoryRepository extends MongoRepository<StatusHistory, String> {}
