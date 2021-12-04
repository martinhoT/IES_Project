package com.prototype.app.controller;

import java.util.HashMap;

import javax.validation.Valid;

import com.prototype.app.entity.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("")
@Controller
public class MainController {

	@GetMapping("/")
	public String main(Model model) {
        model.addAttribute("text", "Hello World!");
		return "main";
	}

	@GetMapping("/login")
    public String showLoginForm(User user) {
        return "login";
    }

	@PostMapping("/login")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

		if (user.getName().equals("Analyst") && user.getpassword().equals("Password")){
			return "redirect:/api";
		}else if (user.getName().equals("Security") && user.getpassword().equals("Password")){
			return "redirect:/guard";
		}else if (user.getName().equals("Student") && user.getpassword().equals("Password")) {
			return "redirect:/";
		}else{
			model.addAttribute("wrong", true);
			return "login";
		}
    }

	@GetMapping("/guard")
	public String guard(Model model) {
		return "guard";
	}


    @GetMapping("/error")
	public String error() {
		return "error";
	}
}