package com.getaroom.app.repository.mysql;

import java.util.List;

import com.getaroom.app.entity.mysql.Room;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {

    List<Room> findByDepId(int depId);
}
