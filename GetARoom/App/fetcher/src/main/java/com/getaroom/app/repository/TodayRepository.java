package com.getaroom.app.repository;

import com.getaroom.app.entity.Event;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodayRepository extends MongoRepository<Event, String> {}
