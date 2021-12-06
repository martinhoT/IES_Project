package com.prototype.app.controller;


import com.prototype.app.entity.Event;
import com.prototype.app.entity.Room;
import com.prototype.app.entity.Student;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import javax.validation.Valid;
import com.prototype.app.entity.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RequestMapping("")
@Controller
public class MainController {

	private final Map<String, Student> blacklist;

	public MainController() {
		blacklist = new HashMap<>(Map.of(
				"petersonkidd@cytrex.com", new Student("Peterson Kidd", "petersonkidd@cytrex.com"),
				"alfordnicholson@cytrex.com", new Student("Alford Nicholson", "alfordnicholson@cytrex.com"),
				"doramcneil@cytrex.com", new Student("Dora Mcneil", "doramcneil@cytrex.com")
		));
	}

	public Map<String, Student> getRoomBlacklist(int dep, int floor, int room) {
		return blacklist;
	}

	public Map<String, List<Event>> getRoomEventMap(int dep, int floor, int room) {
		return ApiController.getHistory("2020");
	}

	public void remRoomBlacklist(int dep, int floor, int room, String studentEmail) {
		blacklist.remove(studentEmail);
	}

	public void addRoomBlacklist(int dep, int floor, int room, String studentName, String studentEmail) {
		if (!blacklist.containsKey(studentEmail))
			blacklist.put(studentEmail, new Student(studentName, studentEmail));
	}

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
			return "redirect:/sec";
		}else if (user.getName().equals("Student") && user.getpassword().equals("Password")) {
			return "redirect:/studyRooms";
		}else{
			model.addAttribute("wrong", true);
			return "login";
		}
	}

	@GetMapping("/sec")
	public String sec() {
		return "sec";
	}

	@GetMapping("/logs")
	public String logs(@RequestParam(defaultValue = "None") String room, Model model) {
		List<Event> events;
		if (room.equals("None"))
			events = new ArrayList<>();
		else
			events = ApiController.getToday(room);
		model.addAllAttributes(Map.of(
			"events", events
		));
		return "logs";
	}

	@GetMapping("/room/{dep}.{floor}.{room}")
	public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
		/*
		* ... obtain the room dynamically ...
		*/

		Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
		Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
		model.addAllAttributes(Map.of(
			"dep", dep,
			"floor", floor,
			"room", room,
			"blacklisted", blacklisted,
			"eventMap", eventMap
		));
		return "room";
	}

	@GetMapping("/room/{dep}.{floor}.{room}/remove")
	public String roomRemove(
			@PathVariable int dep,
			@PathVariable int floor,
			@PathVariable int room,
			@RequestParam(name="email") String studentEmail,
			Model model) {

		Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
		Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
		model.addAllAttributes(Map.of(
				"dep", dep,
				"floor", floor,
				"room", room,
				"blacklisted", blacklisted,
				"eventMap", eventMap
		));
		remRoomBlacklist(dep, floor, room, studentEmail);
		return "room";
	}

	@GetMapping("/room/{dep}.{floor}.{room}/add")
	public String roomAdd(
			@PathVariable int dep,
			@PathVariable int floor,
			@PathVariable int room,
			@RequestParam(name="name") String studentName,
			@RequestParam(name="email") String studentEmail,
			Model model) {

		Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
		Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
		model.addAllAttributes(Map.of(
				"dep", dep,
				"floor", floor,
				"room", room,
				"blacklisted", blacklisted,
				"eventMap", eventMap
		));
		addRoomBlacklist(dep, floor, room, studentName, studentEmail);
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

	@GetMapping("/heatmaps_lite")
	public String heatmaps_lite(Model model) {
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
		return "heatmaps_lite";
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