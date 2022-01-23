package com.getaroom.app.repository.mongodb;

import java.util.List;

import com.getaroom.app.entity.mongodb.RoomStyle;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoomStyleRepository extends MongoRepository<RoomStyle, String> {

    @Query(value = "{room:{$regex:'?0\\\\.?1\\\\.[0-9]+'}}")
    List<RoomStyle> findAllRooms(String depId, String floor);
}
