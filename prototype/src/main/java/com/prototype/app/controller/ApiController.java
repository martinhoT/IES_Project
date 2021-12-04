package com.prototype.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ApiController {

	@GetMapping("/api/demo")
	public String demo() {
		return "{'Hello': 'World'}";
    }
}