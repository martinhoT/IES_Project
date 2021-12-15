package com.brokerclient.app;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodayRepository extends MongoRepository<Today, String> {}
