package com.getaroom.app.controller;

import java.io.File;
import java.io.FileReader;
import javax.validation.Valid;
import javax.validation.constraints.Null;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Status;
import com.getaroom.app.entity.User;
import com.getaroom.app.repository.RoomRepository;
import com.mongodb.client.FindIterable;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
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

	private Map<String, List<Status>> allRooms = new HashMap<String, List<Status>>();

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
		System.err.println(allDepartments);

		//Collections.sort(allDepartments);
		model.addAttribute("depList", allDepartments);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){

		List<Status> allRooms = roomRepository.findAllRooms(dep);
		System.err.println(allRooms);

		JSONParser parser = new JSONParser();
		JSONObject suggested = null;
		ModelAndView mav = new ModelAndView();
		List<Status> rooms = new ArrayList<>();


		try {

			JSONArray a = (JSONArray) parser.parse(new FileReader("src/main/resources/static/data/status/" + dep.toLowerCase() + ".json"));

			for (Object o : a)
			{
				Status room = new Status();
				JSONObject roomJson = (JSONObject) o;

				String roomName = (String) roomJson.get("room");
				room.setRoom(roomName);

				String roomOccupacy = (String) roomJson.get("occupacy");
				room.setOccupacy(roomOccupacy);

				Long roomMaxNumberOfPeople = (Long) roomJson.get("maxNumberOfPeople");
				room.setMaxNumberOfPeople(roomMaxNumberOfPeople);

				Boolean roomRestricted = (Boolean) roomJson.get("restricted");
				room.setRestricted(roomRestricted);

				if (!room.getRestricted())
					rooms.add(room);
//
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

		rooms.sort(Comparator.comparingInt(Status::getCurrentOccupacy));

		mav.addObject("rooms", rooms.stream().limit(10).collect(Collectors.toList()));
		mav.setViewName("suggested_room");

		return mav;
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
}