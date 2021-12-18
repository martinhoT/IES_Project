package com.getaroom.app.controller;

import java.io.File;
import java.io.FileReader;
import javax.validation.Valid;
import javax.validation.constraints.Null;

import com.getaroom.app.entity.Room;
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
		// Get dep names

		// Possibly like this
		Map<String, List<String>> allRooms = new HashMap<String, List<String>>();
		FindIterable<Document> rooms = roomRepository.findAllRooms();
		for(Document doc : rooms){
			String room = ((String) doc.get("room"));
			String[] room_info = room.split(".");
			if(allRooms.get(room_info[0])== null){
				allRooms.put(room_info[0], new ArrayList<String>());
			}
			else{
				allRooms.get(room_info[0]).add(room);
			}
        }


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

//				Boolean roomRestricted = (Boolean) roomJson.get("restricted");
//				room.setRestricted(roomRestricted);
//
//				if (!room.getRestricted())
//					rooms.add(room);
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

		rooms.sort(Comparator.comparingInt(Room::getCurrentOccupacy));

		mav.addObject("rooms", rooms.stream().limit(10).collect(Collectors.toList()));
		mav.setViewName("suggested_room");

		return mav;
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}
}