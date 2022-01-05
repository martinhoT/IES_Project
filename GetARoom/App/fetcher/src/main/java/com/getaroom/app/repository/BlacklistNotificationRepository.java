package com.getaroom.app.repository;

import com.getaroom.app.entity.BlacklistNotification;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistNotificationRepository extends MongoRepository<BlacklistNotification, String> {}
