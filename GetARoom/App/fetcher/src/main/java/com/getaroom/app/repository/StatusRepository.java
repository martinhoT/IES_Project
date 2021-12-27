package com.getaroom.app.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Room;

@Repository
public interface StatusRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoom(String room);

    // @Query(value = "{}", fields = "{_id:0, dep: {$arrayElemAt: [{$split: ['$room', '.']}, 0]}}")
    @Aggregation(pipeline = {"{$project: {dep: {$arrayElemAt: [{$split: ['$room', '.']}, 0]}}}","{$group:{_id:'$dep'}}","{$project: {dep:'$_id'}}"})
    List<Dep> findAllDep();

    // @Query(value = "{room:{$regex:'?0\\\\.[0-9]+\\\\.[0-9]+'}}", fields = "{'_id':0,'_class':0})")
    @Query(value = "{room:{$regex:'?0\\\\.[0-9]+\\\\.[0-9]+'}}")
    List<Room> findAllRooms(String depId);
}
