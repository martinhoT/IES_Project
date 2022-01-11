package com.getaroom.app.repository;

import java.util.Optional;

import com.getaroom.app.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends JpaRepository<Blacklist, String> {

    Optional<Blacklist> findByRoom_idAndEmail(String room_id, String email);

}
