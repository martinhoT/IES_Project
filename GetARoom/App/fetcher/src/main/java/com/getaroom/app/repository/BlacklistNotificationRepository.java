package com.getaroom.app.repository;

import java.util.Date;
import java.util.Optional;

import com.getaroom.app.entity.BlacklistNotification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistNotificationRepository extends MongoRepository<BlacklistNotification, String> {

    Optional<BlacklistNotification> findByEmailAndRoomAndTime(String email, String room, Date time);
}
