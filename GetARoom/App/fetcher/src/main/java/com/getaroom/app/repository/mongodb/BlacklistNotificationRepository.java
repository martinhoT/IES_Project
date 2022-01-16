package com.getaroom.app.repository.mongodb;

import java.util.Date;
import java.util.List;

import com.getaroom.app.entity.mongodb.BlacklistNotification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistNotificationRepository extends MongoRepository<BlacklistNotification, String> {

    List<BlacklistNotification> findByEmailAndRoomAndTime(String email, String room, Date time);
}
