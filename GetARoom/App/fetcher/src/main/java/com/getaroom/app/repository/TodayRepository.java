package com.getaroom.app.repository;

import java.util.List;

import com.getaroom.app.entity.Today;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodayRepository extends MongoRepository<Today, String> {
    List<Today> findByRoom(String room);
}
