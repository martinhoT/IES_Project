package com.getaroom.app.controller;

import com.getaroom.app.other.LoginData;
import com.getaroom.app.other.RegisterData;
import com.getaroom.app.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    private final UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginData loginData) {
        return ResponseEntity.ok().body(
            userRepository.loggeIn(loginData.getLogin(), loginData.getPassword(), loginData.getRole()) == 1
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody RegisterData registerData) {
        return ResponseEntity.ok().body(
            userRepository.register(registerData.getUsername(), registerData.getEmail(), registerData.getPassword(), registerData.getRole()) == 1
        );
    }
}
