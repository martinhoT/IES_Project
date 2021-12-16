package com.getaroom.app.controller;

import javax.validation.Valid;

import com.getaroom.app.repository.UserRepository;
import com.getaroom.app.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController 
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("")
    public ModelAndView showLoginForm(User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @PostMapping("")
    public ModelAndView login(@Valid User user, BindingResult result, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors()) {
            modelAndView.setViewName("login");
        }
        if (userRepository.loggeIn(user.getName(), user.getpassword())==1){
            modelAndView.setViewName("redirect:/studyRooms");
        }
        else{
            modelAndView.setViewName("login");
            model.addAttribute("wrong", true);
        }
        return modelAndView;
    }
}