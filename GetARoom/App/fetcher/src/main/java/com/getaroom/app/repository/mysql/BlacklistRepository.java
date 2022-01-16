package com.getaroom.app.repository.mysql;

import java.util.Optional;

import com.getaroom.app.entity.mysql.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.getaroom.app.entity.mysql.Room;
import java.util.List;


@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {

    Optional<Blacklist> findByRoomIdAndEmail(String roomId, String email);

    boolean existsByRoomIdAndEmail(String roomId, String email);

    List<Blacklist> findByRoomId(String roomId);

}
