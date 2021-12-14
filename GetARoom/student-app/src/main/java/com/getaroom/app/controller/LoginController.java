package com.getaroom.app.controller;


import com.getaroom.app.entity.Room;
import com.getaroom.app.entity.Student;

import java.io.File;
import java.io.FileReader;
import javax.validation.Valid;
import com.getaroom.app.entity.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RequestMapping("/login")
@Controller
public class LoginController {
    @GetMapping("")
    public String showLoginForm(User user) {
        return "login";
    }

    @PostMapping("")
    public String login(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        if (user.getName().equals("Student") && user.getpassword().equals("Password")) {
            return "redirect:/studyRooms";
        }else{
            model.addAttribute("wrong", true);
            return "login";
        }
    }
}