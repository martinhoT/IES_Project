package com.getaroom.app.repository;

import java.util.List;

import com.getaroom.app.entity.EventNow;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<EventNow, String> {
    List<EventNow> findByRoom(String room);
}
