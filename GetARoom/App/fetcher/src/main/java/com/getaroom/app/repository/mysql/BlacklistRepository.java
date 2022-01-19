package com.getaroom.app.repository.mysql;

import java.util.Optional;

import com.getaroom.app.entity.mysql.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {

    Optional<Blacklist> findByRoomIdAndEmail(String roomId, String email);

    boolean existsByRoomIdAndEmail(String roomId, String email);

    List<Blacklist> findByRoomId(String roomId);

    @Query(value = "select b from Blacklist b where b.roomId in (select r.id from Room r where r.depId = :id) order by b.roomId")
    List<Blacklist> blacklistForDepartment(int id);

}
