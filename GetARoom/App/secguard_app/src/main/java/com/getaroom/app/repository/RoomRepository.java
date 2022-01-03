package com.getaroom.app.repository;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Status;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface RoomRepository extends MongoRepository<Status, String> {

    // @Query(value = "{}", fields = "{_id:0, dep: {$arrayElemAt: [{$split: ['$room', '.']}, 0]}}")
    @Aggregation(pipeline = {"{$project: {_id:0, dep: {$arrayElemAt: [{$split: ['$room', '.']}, 0]}}}","{$group:{_id:'$dep'}}","{$project: {dep:'$_id'}}"})
    List<Dep> findAllDep();

    @Query(value = "{room:{$regex:'?0\\\\.[0-9]+\\\\.[0-9]+'}}", fields = "{'_id':0,'_class':0})")
    List<Status> findAllRooms(String depId);

}
