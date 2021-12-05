package com.prototype.app.controller;

import java.io.FileReader;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.validation.Valid;

import com.prototype.app.entity.Room;
import com.prototype.app.entity.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
		// dep = 4;
		// floor = 1;
		// room = 19;
		model.addAllAttributes(Map.of(
			"dep", dep,
			"floor", floor,
			"room", room
		));
		return "room";
	}

	@GetMapping("/heatmaps")
	public String heatmaps(Model model) {
		Map<String,String> roomOccupacy = new HashMap<String,String>();
		for(int department = 1; department <= 6; department++){
			JSONParser parser = new JSONParser();
			try {
				java.io.File filePath = new java.io.File("src/main/resources/static/data/status/dep"+department+".json");
				JSONArray jsonRooms = (JSONArray) parser.parse(new FileReader(filePath));
				for(Object roomJson : jsonRooms){
					JSONObject jsonObject = (JSONObject)roomJson;
					roomOccupacy.put((String)jsonObject.get("room"),(String)jsonObject.get("occupacy"));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		model.addAllAttributes(Map.of(
			"roomOccupacy", roomOccupacy));
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