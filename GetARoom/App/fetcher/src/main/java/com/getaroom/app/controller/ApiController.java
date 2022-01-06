package com.getaroom.app.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.*;
import com.getaroom.app.repository.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final StatusRepository statusRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final EventRepository eventRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final RoomStyleRepository roomStyleRepository;
    private final BlacklistRepository blacklistRepository;

    @Autowired
    public ApiController(StatusRepository statusRepository, StatusHistoryRepository statusHistoryRepository, EventRepository eventRepository, RoomStyleRepository roomStyleRepository, EventHistoryRepository eventHistoryRepository, BlacklistRepository blacklistRepository) {
        this.statusRepository = statusRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.eventRepository = eventRepository;
        this.roomStyleRepository = roomStyleRepository;
        this.eventHistoryRepository = eventHistoryRepository;
        this.blacklistRepository = blacklistRepository;

        // TODO: Better updates? (check if modified?)
        if (roomStyleRepository.count() == 0) {
            Gson gson = new Gson();
            FileReader f = null;
            try {
                f = new FileReader("src/main/resources/static/data/room_styles.json");
            } catch (FileNotFoundException e) {
                System.err.println("File with room styles (room_styles.json) could not be read");
            }

            if (f != null) {
                JsonArray roomStylesList = gson.fromJson(f, JsonArray.class);
                for (JsonElement style : roomStylesList)
                    roomStyleRepository.save( gson.fromJson(style, RoomStyle.class) );
            }
        }
    }

    @GetMapping("/today")
    public List<EventNow> today(@RequestParam(defaultValue = "") String room) {
        if (room.isEmpty()){
            return eventRepository.findAll();
        }else{
            return eventRepository.findByRoom(room);
        }
    }

    @GetMapping("/today_history")
    public List<EventHistory> todayHistory() {
        return eventHistoryRepository.findAll();
    }

    @GetMapping("/status_history")
    public List<StatusHistory> statusHistory() {
        return statusHistoryRepository.findAll();
    }

    @GetMapping("/status")
    public Map<String, List<StatusNow>> status(@RequestParam(required = false) String dep) {
        // Used by student_app and analyst_app
        if (dep != null)
            return Map.of(dep, statusRepository.findAllRooms(dep));

        return null;
    }

    @CrossOrigin
    @GetMapping("/department")
    public List<Dep> department() {
        return statusRepository.findAllDep();
    }

    // Used in heatmaps
    @CrossOrigin
    @GetMapping("/roomStyles")
    public List<RoomStyle> roomStyles(
        @RequestParam(required = true, defaultValue = "") String dep,
        @RequestParam(required = true, defaultValue = "") String floor) {

        // if (dep.isEmpty() || floor.isEmpty())
        //     return roomStyleRepository.findAll();
        return roomStyleRepository.findAllRooms(dep, floor);
    }

    @CrossOrigin
    @GetMapping(value="/getRooms")
    @ResponseBody
    public ArrayList<String> setRoomValuesByDepartment(@RequestParam("Result") String res, Model model) {
        System.out.println(res);
        ArrayList<String> results = new ArrayList<>();

        List<StatusNow> x = statusRepository.findAllRooms(res);
        x.forEach(elem -> results.add(elem.getRoom()));

        System.out.println(res);
        System.out.println("Success");

        return results;
    }

    @CrossOrigin
    @PostMapping(value = "/addUserBlacklist")
    @ResponseBody
    public String addRoomBlacklist(@RequestParam("Room") String room, @RequestParam("Email") String studentEmail) {
        blacklistRepository.save(new Blacklist(studentEmail, room));

        //blacklist.forEach((k, v) -> System.out.println(k + ", " + v));

        return "success";
    }

    @CrossOrigin
    @PostMapping(value = "/removeUserBlacklist")
    @ResponseBody
    public String remRoomBlacklist(@RequestParam("Room") String room, @RequestParam("Email") String studentEmail) {
        try{
            blacklistRepository.delete(new Blacklist(studentEmail, room));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        //blacklist.forEach((k, v) -> System.out.println(k + ", " + v));

        return "success";
    }

}
