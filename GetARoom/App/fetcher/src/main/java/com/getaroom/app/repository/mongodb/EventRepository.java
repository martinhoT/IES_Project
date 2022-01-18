package com.getaroom.app.repository.mongodb;

import com.getaroom.app.entity.mongodb.EventNow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends MongoRepository<EventNow, String> {
    Page<EventNow> findByRoom(String room, Pageable pageable);
}
