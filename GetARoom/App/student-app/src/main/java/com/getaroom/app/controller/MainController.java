package com.getaroom.app.controller;


import com.getaroom.app.entity.Room;
import com.getaroom.app.entity.Student;

import java.io.File;
import java.io.FileReader;
import javax.validation.Valid;
import com.getaroom.app.entity.User;
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

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
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

		if (user.getName().equals("Student") && user.getpassword().equals("Password")) {
			return "redirect:/studyRooms";
		}else{
			model.addAttribute("wrong", true);
			return "login";
		}
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

	@GetMapping(value="/studyRooms")
	public String getAllDepartments(Model model) {
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
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){
		JSONParser parser = new JSONParser();
		JSONObject suggested = null;
		ModelAndView mav = new ModelAndView();
		List<Room> rooms = new ArrayList<>();


		try {

			JSONArray a = (JSONArray) parser.parse(new FileReader("src/main/resources/static/data/status/" + dep.toLowerCase() + ".json"));

			for (Object o : a)
			{
				Room room = new Room();
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

		rooms.sort(Comparator.comparingInt(Room::getCurrentOccupacy));

		mav.addObject("rooms", rooms);
		mav.setViewName("suggested_room");

		return mav;
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
}