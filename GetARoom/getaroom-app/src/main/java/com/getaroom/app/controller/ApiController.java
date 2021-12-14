package com.getaroom.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import student_app.src.main.java.com.getaroom.app.entity.Room;
import student_app.src.main.java.com.getaroom.app.entity.Event;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import java.io.FileReader;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@RestController
public class ApiController {
	// TODO: instead of these methods being static, make requests to this API

	public static List<Event> getToday(String roomName){
		JSONParser parser = new JSONParser();
		List<Event> eventList = new ArrayList<Event>();

		try {
			java.io.File filePath = new java.io.File("src/main/resources/static/data/today/events.json");
			JSONArray jsonEvents = (JSONArray) parser.parse(new FileReader(filePath));
			for(Object eventJson : jsonEvents){
				JSONObject jsonObject = (JSONObject)eventJson;
				Event event = new Event(
										(Long)jsonObject.get("index"), 
										(String)jsonObject.get("user"), 
										(String)jsonObject.get("email"),
										(String)jsonObject.get("room"),
										(Boolean)jsonObject.get("entered"), 
										(String)jsonObject.get("time")
									   );
				if (roomName == null){
					eventList.add(event);
				}else if(event.getRoom().replace(".", "").equals(roomName)){
					eventList.add(event);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return eventList;
	}

	public static HashMap<String, List<Event>> getHistory(String year){
		HashMap<String, List<Event>> history = new HashMap<String, List<Event>>();

		if (year != null){
			JSONParser parser = new JSONParser();
			List<Event> eventList = new ArrayList<Event>();
			try {
				java.io.File filePath = new java.io.File("src/main/resources/static/data/history/" + year + ".json");
				JSONArray jsonEvents = (JSONArray) parser.parse(new FileReader(filePath));
				for(Object eventJson : jsonEvents){
					JSONObject jsonObject = (JSONObject)eventJson;
					Event event = new Event(
											(Long)jsonObject.get("index"), 
											(String)jsonObject.get("user"), 
											(String)jsonObject.get("email"),
											(String)jsonObject.get("room"),
											(Boolean)jsonObject.get("entered"), 
											(String)jsonObject.get("time")
										);
					eventList.add(event);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			history.put(year + "", eventList);
		}else {
			for(int y = 2018; y <= 2020; y++){
				JSONParser parser = new JSONParser();
				List<Event> eventList = new ArrayList<Event>();
				try {
					java.io.File filePath = new java.io.File("src/main/resources/static/data/history/" + y + ".json");
					JSONArray jsonEvents = (JSONArray) parser.parse(new FileReader(filePath));
					for(Object eventJson : jsonEvents){
						JSONObject jsonObject = (JSONObject)eventJson;
						Event event = new Event(
												(Long)jsonObject.get("index"), 
												(String)jsonObject.get("user"), 
												(String)jsonObject.get("email"),
												(String)jsonObject.get("room"),
												(Boolean)jsonObject.get("entered"), 
												(String)jsonObject.get("time")
											);
						eventList.add(event);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				history.put(y + "", eventList);
			}
		}
		return history;
	}

	public static HashMap<String, List<Room>> getStatus(){
		HashMap<String, List<Room>> departments = new HashMap<String, List<Room>>();
		for(int department = 1; department <= 6; department++){
			JSONParser parser = new JSONParser();
			List<Room> roomList = new ArrayList<Room>();
			try {
				java.io.File filePath = new java.io.File("src/main/resources/static/data/status/dep" + department + ".json");
				JSONArray jsonRooms = (JSONArray) parser.parse(new FileReader(filePath));
				for(Object roomJson : jsonRooms){
					JSONObject jsonObject = (JSONObject)roomJson;
					Room room = new Room(
											(String)jsonObject.get("room"),
											(String)jsonObject.get("occupacy"),
											(Long)jsonObject.get("maxNumberOfPeople"),
											(Boolean)jsonObject.get("restricted")
										);
					roomList.add(room);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			departments.put("Department " + department, roomList);
		}
		return departments;
	}

	@GetMapping("/api/today")
	public List<Event> today(@RequestParam(required = false) String room) {
		return getToday(room);
    }

	@GetMapping("/api/history")
	public HashMap<String, List<Event>> history(@RequestParam(required = false) String year) {
		return getHistory(year);
    }

	@GetMapping("/api/status")
	public HashMap<String, List<Room>> status() {
		return getStatus();
    }
}