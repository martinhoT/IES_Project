package com.getaroom.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import student_app.src.main.java.com.getaroom.app.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    @Procedure
    boolean loggeIn(String username, String password);

    @Procedure
    String register(String username, String email, String password, String role);
}
