package com.prototype.app.controller;

import com.prototype.app.entity.Event;
import com.prototype.app.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("")
@Controller
public class MainController {

	private final List<Student> blacklist;

	public MainController() {
		blacklist = new ArrayList<>(List.of(
				new Student(98592),
				new Student(92521),
				new Student(52195)
		));
	}

	public List<Student> getRoomBlacklist(int dep, int floor, int room) {
		return blacklist;
	}

	public HashMap<String, List<Event>> getRoomEventMap(int dep, int floor, int room) {
		return ApiController.getHistory();
	}

	public void remRoomBlacklist(int dep, int floor, int room, int studentId) {
		blacklist.removeIf((s) -> s.getId()==studentId);
	}

	@GetMapping("/")
	public String main(Model model) {
        model.addAttribute("text", "Hello World!");
		return "main";
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
		dep = 4;
		floor = 1;
		room = 19;

		List<Student> blacklisted = getRoomBlacklist(dep, floor, room);
		HashMap<String, List<Event>> eventMap = getRoomEventMap(dep, floor, room);
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
			@RequestParam(name="sid") int studentId) {

		remRoomBlacklist(dep, floor, room, studentId);
		return "room";
	}

	@GetMapping("/heatmaps")
	public String heatmaps() {
		return "heatmaps";
	}

	@GetMapping("/api")
	public String guard() {
		return "api";
	}

    @GetMapping("/error")
	public String error() {
		return "error";
	}
}