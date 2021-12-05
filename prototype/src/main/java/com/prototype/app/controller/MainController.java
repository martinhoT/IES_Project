package com.prototype.app.controller;

import com.prototype.app.entity.Event;
import com.prototype.app.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		return ApiController.getHistory();
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

	@GetMapping("/logs")
	public String logs() {
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