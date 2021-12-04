package com.prototype.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("")
@Controller
public class MainController {

	@GetMapping("/")
	public String main(Model model) {
        model.addAttribute("text", "Hello World!");
		return "main";
	}

    @GetMapping("/error")
	public String error() {
		return "error";
	}
}