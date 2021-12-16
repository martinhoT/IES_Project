package com.getaroom.app.repository;

import com.getaroom.app.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoomRepository extends MongoRepository<Room, String> {



}
