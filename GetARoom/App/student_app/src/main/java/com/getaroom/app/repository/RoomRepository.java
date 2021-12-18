package com.getaroom.app.repository;

import com.getaroom.app.entity.Room;
import com.mongodb.client.FindIterable;

import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    @Query(value = "db.status.find({},{'room':1,'id':0})")
    FindIterable<Document> findAllRooms();

}
