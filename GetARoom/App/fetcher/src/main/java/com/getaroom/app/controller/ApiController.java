package com.getaroom.app.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.BlacklistNotification;
import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.EventHistory;
import com.getaroom.app.entity.EventNow;
import com.getaroom.app.entity.StatusHistory;
import com.getaroom.app.entity.StatusNow;
import com.getaroom.app.entity.RoomStyle;
import com.getaroom.app.repository.BlacklistNotificationRepository;
import com.getaroom.app.repository.EventHistoryRepository;
import com.getaroom.app.repository.RoomStyleRepository;
import com.getaroom.app.repository.StatusHistoryRepository;
import com.getaroom.app.repository.StatusRepository;
import com.getaroom.app.repository.EventRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final StatusRepository statusRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final EventRepository eventRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final RoomStyleRepository roomStyleRepository;
    private final BlacklistNotificationRepository blacklistNotificationRepository;

    @Autowired
    public ApiController(
        StatusRepository statusRepository, 
        StatusHistoryRepository statusHistoryRepository, 
        EventRepository eventRepository, 
        RoomStyleRepository roomStyleRepository, 
        EventHistoryRepository eventHistoryRepository,
        BlacklistNotificationRepository blacklistNotificationRepository) {
            
        this.statusRepository = statusRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.eventRepository = eventRepository;
        this.roomStyleRepository = roomStyleRepository;
        this.eventHistoryRepository = eventHistoryRepository;
        this.blacklistNotificationRepository = blacklistNotificationRepository;

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
    @GetMapping("/room_styles")
    public List<RoomStyle> roomStyles(
        @RequestParam(required = true, defaultValue = "") String dep,
        @RequestParam(required = true, defaultValue = "") String floor) {

        // if (dep.isEmpty() || floor.isEmpty())
        //     return roomStyleRepository.findAll();
        return roomStyleRepository.findAllRooms(dep, floor);
    }

    @CrossOrigin
    @GetMapping("/alerts/unseen")
    public List<BlacklistNotification> unseenNotifications() {
        return blacklistNotificationRepository.findAll();
    }

    @CrossOrigin
    @PostMapping("/alerts/mark_read")
    public void markRead(@RequestBody List<BlacklistNotification> notifications) {
        blacklistNotificationRepository.deleteAll( notifications );
    }
}
