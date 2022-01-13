package com.getaroom.app.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.getaroom.app.entity.mongodb.Status;

/**
 * This repository contains the rooms' historic occupancy data.
 */
@Repository
public interface StatusRepository extends MongoRepository<Status, String> {

    Optional<Status> findByRoom(String room);

}
