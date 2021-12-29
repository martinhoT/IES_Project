package com.getaroom.app.controller;

import com.getaroom.app.entity.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RequestMapping("")
@Controller
public class MainController {

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
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