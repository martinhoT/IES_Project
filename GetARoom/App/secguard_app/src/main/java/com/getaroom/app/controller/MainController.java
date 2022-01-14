package com.getaroom.app.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;
import com.getaroom.app.entity.Student;
import com.getaroom.app.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RequestMapping("")
@Controller
public class MainController {

	private final Map<String, Student> blacklist;
	private final WebClient apiClient;

	@Autowired
	public MainController() {
		apiClient = WebClient.create("http://fetcher:8080");
		blacklist = new HashMap<>(Map.of(
				"petersonkidd@cytrex.com", new Student("Peterson Kidd", "petersonkidd@cytrex.com"),
				"alfordnicholson@cytrex.com", new Student("Alford Nicholson", "alfordnicholson@cytrex.com"),
				"doramcneil@cytrex.com", new Student("Dora Mcneil", "doramcneil@cytrex.com")
		));
	}

	public Map<String, Student> getRoomBlacklist(int dep, int floor, int room) {
		return blacklist;
	}

	// public Map<String, List<Event>> getRoomEventMap(int dep, int floor, int room) {
	// 	return ApiController.getHistory("2020");
	// }

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
	
	// @GetMapping("/sec")
	// public String sec() {
	// 	return "sec";
	// }

	@GetMapping("/blacklist")
	public ModelAndView getBlacklist(Model model){
		ModelAndView mav = new ModelAndView();

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		Collections.sort(allDepartments, Comparator.comparing(Dep::getId).reversed());

		model.addAttribute("depList", allDepartments);

		mav.setViewName("blacklist");

		return mav;
	}

	@GetMapping("/logs")
	public String logs(@RequestParam(defaultValue = "None") String room, Model model) {
		List<Event> RoomEvents = apiGetRequestList("event", Event.class);
		System.err.println(RoomEvents);
	// 	List<Event> events;
	// 	if (room.equals("None"))
	// 		events = new ArrayList<>();
	// 	else
	// 		events = ApiController.getToday(room);
		model.addAllAttributes(Map.of(
			"events", RoomEvents
		));
		return "logs";
	}

	@GetMapping("/room/{dep}.{floor}.{room}")
	public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
	// 	/*
	// 	* ... obtain the room dynamically ...
	// 	*/
		String currentRoom = String.valueOf(dep) + "." + String.valueOf(floor) + "." + String.valueOf(room);
		List<Event> currentRoomEvents = new ArrayList<Event>();
		List<Event> RoomEvents = apiGetRequestList("event", Event.class);
		for (Event e : RoomEvents){
			if (e.getRoom().equals(currentRoom)) currentRoomEvents.add(e);
		}
	// List<Event> RoomEvents = apiRoomLogs(currentRoom);
		Map<String, Student> blacklisted = new HashMap<String, Student>();
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
		model.addAllAttributes(Map.of(
			 "dep", dep,
			 "floor", floor,
			 "room", room,
			 "blacklisted", blacklisted,
			"events", currentRoomEvents
		));
		return "room";
	}

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


	@GetMapping("/heatmaps")
	public String heatmaps(Model model) {
		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		model.addAllAttributes(Map.of(
			"departments", allDepartments));
		return "heatmaps";
	}

	@GetMapping("/notifications")
	public String notifications() {
		return "notifications";
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

	// private List<Status> apiStatusDep(String dep) {
	// 	String json = apiClient.get()
	// 		.uri(uriBuilder -> uriBuilder
	// 			.path("/api/status")
	// 			.queryParam("dep", dep)
	// 			.build())
	// 		.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	// 		.acceptCharset(StandardCharsets.UTF_8)
	// 		.exchangeToMono(response -> response.bodyToMono(String.class))
	// 		.block();

	// 	Gson gson = new Gson();
	// 	List<Status> res = new ArrayList<>();
	// 	for (JsonElement elem : gson.fromJson(json, JsonObject.class).getAsJsonArray(dep))
	// 		res.add(gson.fromJson(elem.toString(), Status.class));
	// 	return res;
	// }
}