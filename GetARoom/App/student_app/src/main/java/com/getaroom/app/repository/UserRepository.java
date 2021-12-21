package com.getaroom.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.getaroom.app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(value = "CALL loggeIn(:username, :password);", nativeQuery = true)
    Integer loggeIn(@Param("username") String username,@Param("password") String password);

    @Query(nativeQuery = true, value = "SELECT register(:username, :email, :password, :role);")
    Integer register(@Param("username") String username,@Param("email") String email,@Param("password") String password,@Param("role") String role);
}
