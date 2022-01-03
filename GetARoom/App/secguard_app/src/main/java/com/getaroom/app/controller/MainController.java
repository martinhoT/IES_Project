package com.getaroom.app.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

<<<<<<< HEAD
import com.getaroom.app.entity.*;
import com.getaroom.app.repository.RoomRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
=======
import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;
import com.getaroom.app.entity.Student;
import com.getaroom.app.entity.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("")
@Controller
public class MainController {

<<<<<<< HEAD
	private final RoomRepository roomRepository;
	private final Map<String, List<String>> blacklist;

	public MainController(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
		blacklist = new HashMap<>();
=======
	private final Map<String, Student> blacklist;
	private final WebClient apiClient;

	public MainController() {
		blacklist = new HashMap<>(Map.of(
				"petersonkidd@cytrex.com", new Student("Peterson Kidd", "petersonkidd@cytrex.com"),
				"alfordnicholson@cytrex.com", new Student("Alford Nicholson", "alfordnicholson@cytrex.com"),
				"doramcneil@cytrex.com", new Student("Dora Mcneil", "doramcneil@cytrex.com")
		));
		apiClient = WebClient.create("http://fetcher:8080");
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6
	}

	@GetMapping("/blacklist")
	public ModelAndView getBlacklist(Model model){
		ModelAndView mav = new ModelAndView();

        List<Dep> allDepartments = roomRepository.findAllDep();

		Collections.sort(allDepartments, (o1,o2) -> o1.getdep().compareTo(o2.getdep()));

		model.addAttribute("depList", allDepartments);

		mav.setViewName("blacklist");

		return mav;
	}

	@GetMapping(value="/getRooms")
	@ResponseBody
	public ArrayList<String> setRoomValuesByDepartment(@RequestParam("Result") String res, Model model) {

		ArrayList<String> results = new ArrayList<>();

		List<Status> x = roomRepository.findAllRooms(res);
		x.forEach(elem -> results.add(elem.getRoom()));

		System.out.println(res);
		System.out.println("Success");

		return results;
	}

	// public Map<String, List<Event>> getRoomEventMap(int dep, int floor, int room) {
	// 	return ApiController.getHistory("2020");
	// }

	/*public Map<String, Student> getRoomBlacklist(int dep, int floor, int room) {
		return blacklist;
	}*/

	@PostMapping(value = "/removeUser")
	@ResponseBody
	public String remRoomBlacklist(@RequestParam("Room") String room, @RequestParam("Email") String studentEmail) {

		if (blacklist.containsKey(room))
			blacklist.get(room).remove(studentEmail);

		//blacklist.forEach((k, v) -> System.out.println(k + ", " + v));

		return "success";
	}

	@PostMapping(value = "/addUser")
	@ResponseBody
	public String addRoomBlacklist(@RequestParam("Room") String room, @RequestParam("Email") String studentEmail) {

		if (!blacklist.containsKey(room))
			blacklist.put(room, new ArrayList<>(){{add(studentEmail);}});
		else
			if (!blacklist.get(room).contains(studentEmail))
				blacklist.get(room).add(studentEmail);

		//blacklist.forEach((k, v) -> System.out.println(k + ", " + v));

		return "success";
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}
	
	@GetMapping("/sec")
	public String sec() {
		return "sec";
	}

<<<<<<< HEAD
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
	/*
	@GetMapping("/room/{dep}.{floor}.{room}")
	public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
		*//*
		* ... obtain the room dynamically ...
		*//*
=======
	// @GetMapping("/logs")
	// public String logs(@RequestParam(defaultValue = "None") String room, Model model) {
	// 	List<Event> events;
	// 	if (room.equals("None"))
	// 		events = new ArrayList<>();
	// 	else
	// 		events = ApiController.getToday(room);
	// 	model.addAllAttributes(Map.of(
	// 		"events", events
	// 	));
	// 	return "logs";
	// }

	// @GetMapping("/room/{dep}.{floor}.{room}")
	// public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
	// 	/*
	// 	* ... obtain the room dynamically ...
	// 	*/
>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6

	// 	Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
	// 	Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
	// 	model.addAllAttributes(Map.of(
	// 		"dep", dep,
	// 		"floor", floor,
	// 		"room", room,
	// 		"blacklisted", blacklisted,
	// 		"eventMap", eventMap
	// 	));
	// 	return "room";
	// }

	// @GetMapping("/room/{dep}.{floor}.{room}/remove")
	// public String roomRemove(
	// 		@PathVariable int dep,
	// 		@PathVariable int floor,
	// 		@PathVariable int room,
	// 		@RequestParam(name="email") String studentEmail,
	// 		Model model) {

	// 	Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
	// 	Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
	// 	model.addAllAttributes(Map.of(
	// 			"dep", dep,
	// 			"floor", floor,
	// 			"room", room,
	// 			"blacklisted", blacklisted,
	// 			"eventMap", eventMap
	// 	));
	// 	remRoomBlacklist(dep, floor, room, studentEmail);
	// 	return "room";
	// }

	// @GetMapping("/room/{dep}.{floor}.{room}/add")
	// public String roomAdd(
	// 		@PathVariable int dep,
	// 		@PathVariable int floor,
	// 		@PathVariable int room,
	// 		@RequestParam(name="name") String studentName,
	// 		@RequestParam(name="email") String studentEmail,
	// 		Model model) {

<<<<<<< HEAD
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
*/
=======
	// 	Map<String, Student> blacklisted = getRoomBlacklist(dep, floor, room);
	// 	Map<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
	// 	model.addAllAttributes(Map.of(
	// 			"dep", dep,
	// 			"floor", floor,
	// 			"room", room,
	// 			"blacklisted", blacklisted,
	// 			"eventMap", eventMap
	// 	));
	// 	addRoomBlacklist(dep, floor, room, studentName, studentEmail);
	// 	return "room";
	// }

>>>>>>> 4d14f02be5bc77f13fe67670b4301fa88f1a24e6

	@GetMapping("/heatmaps")
	public String heatmaps(Model model) {
		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);
		// Map<String,String> roomOccupacy = new HashMap<String,String>();
		// for(int department = 1; department <= 6; department++){
		// 	JSONParser parser = new JSONParser();
		// 	try {
		// 		java.io.File filePath = new java.io.File("src/main/resources/static/data/status/dep"+department+".json");
		// 		JSONArray jsonRooms = (JSONArray) parser.parse(new FileReader(filePath));
		// 		for(Object roomJson : jsonRooms){
		// 			JSONObject jsonObject = (JSONObject)roomJson;
		// 			roomOccupacy.put((String)jsonObject.get("room"),(String)jsonObject.get("occupacy"));
		// 		}
		// 	} catch(Exception e) {
		// 		e.printStackTrace();
		// 	}
		// }
		model.addAllAttributes(Map.of(
			"departments", allDepartments));
		return "heatmaps";
	}

    @GetMapping("/error")
	public String error() {
		return "error";
	}

	/**
	 * GET HTTP request to the API located in the fetcher instance.
	 * This version returns a list of results.
	 * 
	 * @param <E>			Generic type representing the class of the objects in the list. Should be the same as the class passed as argument
	 * @param uriAppend		The final location specification on the API. It will essentially be appended to the uri "http://localhost:8080/api/"
	 * @param elementClass	The class of the elements in the list
	 * @return				The list of objects returned by the API call
	 */
	private <E> List<E> apiGetRequestList(String uriAppend, Class<E> elementClass) {
		return apiClient.get()
			.uri("/api/" + uriAppend)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToFlux( response -> response.bodyToFlux(elementClass) )
			.collectList().block();
	}
}