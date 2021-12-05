package com.prototype.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prototype.app.entity.Room;
import com.prototype.app.entity.Event;

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

	public static List<Event> getToday(){
		JSONParser parser = new JSONParser();
		List<Event> eventList = new ArrayList<Event>();
		try {
			java.io.File filePath = new java.io.File("src/main/resources/static/todayEvents.json");
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
		return eventList;
	}

	public static HashMap<String, List<Event>> getHistory(){
		HashMap<String, List<Event>> history = new HashMap<String, List<Event>>();
		for(int year = 2018; year <= 2020; year++){
			JSONParser parser = new JSONParser();
			List<Event> eventList = new ArrayList<Event>();
			try {
				java.io.File filePath = new java.io.File("src/main/resources/static/year" + year + "Events.json");
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
		}
		return history;
	}

	public static HashMap<String, List<Room>> getUsage(){
		HashMap<String, List<Room>> departments = new HashMap<String, List<Room>>();
		for(int department = 1; department <= 6; department++){
			JSONParser parser = new JSONParser();
			List<Room> roomList = new ArrayList<Room>();
			try {
				java.io.File filePath = new java.io.File("src/main/resources/static/dep" + department + "rooms.json");
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
	public List<Event> today() {
		return getToday();
    }

	@GetMapping("/api/history")
	public HashMap<String, List<Event>> history() {
		return getHistory();
    }

	@GetMapping("/api/status")
	public HashMap<String, List<Room>> status() {
		return getUsage();
    }
}