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
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ModelAndView showLoginForm(User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid User user, BindingResult result, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors() && result.getAllErrors().size() > 1)  {
            modelAndView.setViewName("login");
            return modelAndView;
        }
        if (userRepository.loggeIn(user.getName(), user.getpassword(), "security")==1){
            modelAndView.setViewName("redirect:/heatmaps");
        }
        else{
            modelAndView.setViewName("login");
            model.addAttribute("wrong", true);
        }
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid User user, BindingResult result, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors()) {
            modelAndView.setViewName("register");
            return modelAndView;
        }
        if (userRepository.register(user.getName(), user.getEmail(), user.getpassword(),"security") == 1){
            System.err.print("Hey");
            modelAndView.setViewName("redirect:/heatmaps");
        }
        else{
            modelAndView.setViewName("register");
            model.addAttribute("wrong", true);
        }
        return modelAndView;
    }
}