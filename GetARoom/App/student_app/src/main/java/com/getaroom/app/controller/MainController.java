package com.getaroom.app.controller;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Status;
import com.getaroom.app.entity.User;
import com.getaroom.app.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("")
@Controller
public class MainController {

	private final RoomRepository roomRepository;

	@Autowired
	public MainController(RoomRepository roomRepository){
		this.roomRepository = roomRepository;
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}

	@GetMapping(value="/studyRooms")
	public String getAllDepartments(Model model) {

		List<Dep> allDepartments = roomRepository.findAllDep();

		Collections.sort(allDepartments, (o1,o2) -> o1.getdep().compareTo(o2.getdep()));

		model.addAttribute("depList", allDepartments);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){

		List<Status> allRooms = roomRepository.findAllRooms(dep);

		ModelAndView mav = new ModelAndView();

		Collections.sort(allRooms, (o1,o2) -> o1.getOccupacy().compareTo(o2.getOccupacy()));

		mav.addObject("rooms", allRooms.stream().limit(10).collect(Collectors.toList()));
		mav.setViewName("suggested_room");

		return mav;
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
}

