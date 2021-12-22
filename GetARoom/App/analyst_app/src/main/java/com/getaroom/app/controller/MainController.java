package com.getaroom.app.controller;

import javax.validation.Valid;

import com.getaroom.app.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;


@RequestMapping("")
@Controller
public class MainController {

	@GetMapping("/api")
	public String api() {
		return "api";
	}

    @GetMapping("/error")
	public String error() {
		return "error";
	}
}