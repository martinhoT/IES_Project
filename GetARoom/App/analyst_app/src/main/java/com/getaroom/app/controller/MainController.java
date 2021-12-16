package com.getaroom.app.controller;

import javax.validation.Valid;

import com.getaroom.app.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;


@RequestMapping("")
@Controller
public class MainController {

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String showLoginForm(User user) {
		return "login";
	}

	@PostMapping("/login")
	public String login(@Valid User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "login";
		}

		if (user.getName().equals("Analyst") && user.getpassword().equals("Password")){
			return "redirect:/api";
		}else{
			model.addAttribute("wrong", true);
			return "login";
		}
	}

	@GetMapping("/api")
	public String api() {
		return "api";
	}

    @GetMapping("/error")
	public String error() {
		return "error";
	}
}