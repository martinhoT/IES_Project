package com.getaroom.app.repository;

import com.getaroom.app.entity.History;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoryRepository extends MongoRepository<History, String> {}
