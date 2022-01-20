package com.getaroom.app.repository.mysql;

import java.util.Optional;

import com.getaroom.app.entity.mysql.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {

    Optional<Blacklist> findByRoomAndEmail(String room, String email);

    boolean existsByRoomAndEmail(String room, String email);

    List<Blacklist> findByRoom(String room);

    @Query(value = "select b from Blacklist b where b.room in (select r.id from Room r where r.depId = :id) order by b.room")
    List<Blacklist> blacklistForDepartment(int id);

}
