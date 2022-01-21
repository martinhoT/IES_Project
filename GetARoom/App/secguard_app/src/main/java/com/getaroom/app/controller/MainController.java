package com.getaroom.app.controller;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.getaroom.app.entity.Blacklist;
import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;
import com.getaroom.app.entity.Student;
import com.getaroom.app.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMapAdapter;
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
		return "logs";
	}

	@GetMapping("/room/{dep}.{floor}.{room}")
	public String room(@PathVariable int dep, @PathVariable int floor, @PathVariable int room, Model model) {
		String currentRoom = String.valueOf(dep) + "." + String.valueOf(floor) + "." + String.valueOf(room);
		List<Event> currentRoomEvents = new ArrayList<Event>();
		List<Event> RoomEvents = apiGetRequestList("event", Event.class);
		for (Event e : RoomEvents){
			if (e.getRoom().equals(currentRoom)) currentRoomEvents.add(e);
		}
		List<Blacklist> blacklisted = apiGetRequestList("blacklist", Blacklist.class, Map.of(
			"room", List.of(currentRoom)
		));
		model.addAllAttributes(Map.of(
			"dep", dep,
			"floor", floor,
			"room", room,
			"blacklisted", blacklisted,
			"events", currentRoomEvents
		));
		return "room";
	}

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

	private <E> List<E> apiGetRequestList(String uriAppend, Class<E> elementClass, Map<String, List<String>> parameters) {
		
		return apiClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/{append}")
				.queryParams(new MultiValueMapAdapter<>(parameters))
				.build(uriAppend))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToFlux( response -> response.bodyToFlux(elementClass) )
			.collectList().block();
	}

}