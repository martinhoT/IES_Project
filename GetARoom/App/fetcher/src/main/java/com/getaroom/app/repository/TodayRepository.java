package com.getaroom.app.repository;

import com.getaroom.app.entity.Event;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodayRepository extends MongoRepository<Event, String> {}
