package com.prototype.app.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import javax.validation.Valid;

import com.prototype.app.entity.Room;
import com.prototype.app.entity.User;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

	@GetMapping(value="/studyRooms")
	public String getAllStudyRooms(Model model) {
		// Get dep names
		File folder = new File("src/main/resources/static/data/status/");
		File[] listOfFiles = folder.listFiles();
		List<String> fnames = new ArrayList<>();

		for (int i = listOfFiles.length - 1; i >= 0; i--) {
			if (listOfFiles[i].isFile()) {
				fnames.add(listOfFiles[i].getName().replace(".json", "").toUpperCase());
				model.addAttribute(listOfFiles[i].getName().replace(".json", ""), listOfFiles[i].getName());
			}
		}

		Collections.sort(fnames);
		model.addAttribute("depList", fnames);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView addUser(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){
		JSONParser parser = new JSONParser();
		JSONObject suggested = null;
		ModelAndView mav = new ModelAndView();
		Room room = new Room();

		try {

			JSONArray a = (JSONArray) parser.parse(new FileReader("src/main/resources/static/data/status/" + dep.toLowerCase() + ".json"));

			for (Object o : a)
			{
				JSONObject roomJson = (JSONObject) o;

				String roomName = (String) roomJson.get("room");
				room.setRoom(roomName);

				String roomOccupacy = (String) roomJson.get("occupacy");
				room.setOccupacy(roomOccupacy);

				Long roomMaxNumberOfPeople = (Long) roomJson.get("maxNumberOfPeople");
				room.setMaxNumberOfPeople(roomMaxNumberOfPeople);

				Boolean roomRestricted = (Boolean) roomJson.get("restricted");
				room.setRestricted(roomRestricted);

//				if ((Boolean) roomJson.get("restricted"))
//					continue;
//
//
//				if ( suggested == null ||
//						(npeople + (Integer.parseInt(roomOccupacy.substring(0, roomOccupacy.length()-1))/100 *roomMaxNumberOfPeople) <= roomMaxNumberOfPeople)){
//					suggested = roomJson;
//				}

				System.out.println("\n");

			}

		}catch (Exception e) {
			System.out.println(e.getMessage());
		}

		mav.addObject("room", room);
		mav.setViewName("suggested_room");

		return mav;
	}
}