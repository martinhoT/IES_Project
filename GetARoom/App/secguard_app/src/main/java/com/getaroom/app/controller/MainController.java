package com.getaroom.app.controller;


import java.io.FileReader;
import java.util.HashMap;
import javax.validation.Valid;

import com.getaroom.app.entity.*;
import com.getaroom.app.repository.RoomRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("")
@Controller
public class MainController {

	private final RoomRepository roomRepository;
	private final Map<String, Student> blacklist;

	public MainController(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
		blacklist = new HashMap<>(Map.of(
				"petersonkidd@cytrex.com", new Student("Peterson Kidd", "petersonkidd@cytrex.com"),
				"alfordnicholson@cytrex.com", new Student("Alford Nicholson", "alfordnicholson@cytrex.com"),
				"doramcneil@cytrex.com", new Student("Dora Mcneil", "doramcneil@cytrex.com")
		));
	}

	@GetMapping("/blacklist")
	public ModelAndView getBlacklist(Model model){
		ModelAndView mav = new ModelAndView();

        /*List<Dep> allDepartments = roomRepository.findAllDep();

		Collections.sort(allDepartments, (o1,o2) -> o1.getdep().compareTo(o2.getdep()));*/

		ArrayList<String> alist=new ArrayList<>();
		alist.add("Steve");
		alist.add("Tim");
		alist.add("Lucy");
		alist.add("Pat");
		alist.add("Angela");
		alist.add("Tom");

		model.addAttribute("depList", alist);

		mav.setViewName("blacklist");

		return mav;
	}

	@GetMapping(value="/getRooms")
	@ResponseBody
	public ArrayList<String> setRoomValuesByDepartment(@RequestParam("Result") String res, Model model) {

		ArrayList<String> results = new ArrayList<>();

		switch(res) {
			case "Steve":
				results.add("1");
				results.add("2");
				results.add("3");
				results.add("4");
				results.add("5");
				results.add("6");
				break;
			case "Angela":
				results.add("7");
				results.add("8");
				results.add("9");
				results.add("10");
				results.add("11");
				results.add("12");
				break;
			default:
				// code block
		}

		System.out.println(res);
		System.out.println("Success");

		return results;
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
	public String entryPoint(User user) {
		return "redirect:/login";
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

    @GetMapping("/error")
	public String error() {
		return "error";
	}
}