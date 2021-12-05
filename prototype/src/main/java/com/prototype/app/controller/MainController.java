package com.prototype.app.controller;

import javax.validation.Valid;

import com.prototype.app.entity.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

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
	public String login(@Valid User user, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return "login";
		}

		if (user.getName().equals("Analyst") && user.getpassword().equals("Password")){
			return "redirect:/api";
		}else if (user.getName().equals("Security") && user.getpassword().equals("Password")){
			return "redirect:/";
		}else if (user.getName().equals("Student") && user.getpassword().equals("Password")) {
			return "redirect:/";
		}else{
			model.addAttribute("wrong", true);
			return "login";
		}
	}

	@GetMapping("/logs")
	public String logs() {
		return "logs";
	}

	@GetMapping("/room/{dep}.{floor}.{room}")
	public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
		/*
		* ... obtain the room dynamically ...
		*/
		dep = 4;
		floor = 1;
		room = 19;
		model.addAllAttributes(Map.of(
			"dep", dep,
			"floor", floor,
			"room", room
		));
		return "room";
	}

	@GetMapping("/heatmaps")
	public String heatmaps() {
		return "heatmaps";
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