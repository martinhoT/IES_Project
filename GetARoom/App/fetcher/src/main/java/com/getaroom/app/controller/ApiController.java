package com.getaroom.app.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.mongodb.BlacklistNotification;
import com.getaroom.app.entity.mongodb.EventHistory;
import com.getaroom.app.entity.mongodb.EventNow;
import com.getaroom.app.entity.mongodb.RoomStyle;
import com.getaroom.app.entity.mongodb.Status;
import com.getaroom.app.entity.mysql.Blacklist;
import com.getaroom.app.entity.mysql.Department;
import com.getaroom.app.entity.mysql.Room;
import com.getaroom.app.repository.mongodb.BlacklistNotificationRepository;
import com.getaroom.app.repository.mongodb.EventHistoryRepository;
import com.getaroom.app.repository.mongodb.RoomStyleRepository;
import com.getaroom.app.repository.mongodb.StatusRepository;
import com.getaroom.app.repository.mysql.BlacklistRepository;
import com.getaroom.app.repository.mysql.DepartmentRepository;
import com.getaroom.app.repository.mysql.RoomRepository;
import com.getaroom.app.repository.mongodb.EventRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    
    // MongoDB repositories
    private final StatusRepository statusRepository;
    private final EventRepository eventRepository;
    private final EventHistoryRepository eventHistoryRepository;
    private final RoomStyleRepository roomStyleRepository;
    private final BlacklistNotificationRepository blacklistNotificationRepository;
    
    // MySQL
    private final BlacklistRepository blacklistRepository;
    private final DepartmentRepository departmentRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public ApiController(
        StatusRepository statusRepository, 
        EventRepository eventRepository, 
        RoomStyleRepository roomStyleRepository, 
        EventHistoryRepository eventHistoryRepository,
        BlacklistRepository blacklistRepository,
        BlacklistNotificationRepository blacklistNotificationRepository,
        DepartmentRepository departmentRepository,
        RoomRepository roomRepository) {

        this.statusRepository = statusRepository;
        this.eventRepository = eventRepository;
        this.roomStyleRepository = roomStyleRepository;
        this.eventHistoryRepository = eventHistoryRepository;
        this.blacklistRepository = blacklistRepository;
        this.blacklistNotificationRepository = blacklistNotificationRepository;
        this.departmentRepository = departmentRepository;
        this.roomRepository = roomRepository;

        // Only updates if the roomStyle MongoDB collection is dropped
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
    
    @GetMapping("/event")
    public List<EventNow> event(
        @RequestParam(required = false, defaultValue = "") String room,
        @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam(required = false, defaultValue = "20") Integer pageCapacity) {
            
            
            PageRequest pageRequest = PageRequest.of(pageNumber, pageCapacity);
            if (room.isEmpty()){
                return eventRepository.findAll(pageRequest).toList();
            }else{
                return eventRepository.findByRoom(room, pageRequest).toList();
            }
            
        }
    
    @CrossOrigin
    @GetMapping("/event/pages")
    public Integer eventPages() {
        return eventRepository.findAll(Pageable.unpaged()).getTotalPages();
    }

    @CrossOrigin
    @GetMapping("/event_history")
    public List<EventHistory> todayHistory(
        @RequestParam(required = false, defaultValue = "") String room,
        @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
        @RequestParam(required = false, defaultValue = "20") Integer pageCapacity) {

        PageRequest pageRequest = PageRequest.of(pageNumber, pageCapacity);
        return eventHistoryRepository.findAll(pageRequest).toList();
    }

    @GetMapping("/status")
    public List<Status> status() {
        List<Status> res = statusRepository.findAll();
        res.sort(Comparator.comparingLong((s1) -> s1.getTime().getTime()));
        return res;
    }

    @GetMapping("/room")
    public Map<String, List<Room>> room(@RequestParam(required = false) Integer dep) {
        // Used by student_app and analyst_app
        if (dep != null)
            return Map.of(dep.toString(), roomRepository.findByDepId(dep));

        return null;
    }

    @CrossOrigin
    @GetMapping("/department")
    public List<Department> department(@RequestParam(required = false) Integer dep) {
        if (dep != null) {
            Department department = departmentRepository.findById(dep)
                    .orElse(null);
            return department != null ? List.of(department) : new ArrayList<>();
        }
        return departmentRepository.findAll();
    }

    /* FOR THE SECURITY GUARD APP */
    @CrossOrigin
    @GetMapping("/room_styles")
    public List<RoomStyle> roomStyles(
        @RequestParam(required = true, defaultValue = "") String dep,
        @RequestParam(required = true, defaultValue = "") String floor) {

        return roomStyleRepository.findAllRooms(dep, floor);
    }

    @CrossOrigin
    @GetMapping(value="/getRooms")
    @ResponseBody
    public ArrayList<String> setRoomValuesByDepartment(@RequestParam("Result") String res, Model model) {
        System.out.println(res);
        ArrayList<String> results = new ArrayList<>();

        List<Room> x = roomRepository.findByDepId(Integer.parseInt(res));
        x.forEach(elem -> results.add(elem.getId()));

        return results;
    }

    @CrossOrigin
    @PostMapping(value = "/addUserBlacklist")
    @ResponseBody
    public String addRoomBlacklist(@RequestParam("Room") String room, @RequestParam("Email") String studentEmail) {
        blacklistRepository.save(new Blacklist(studentEmail, room));

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

        return "success";
    }

    @CrossOrigin
    @PostMapping(value = "/blacklistByRoom")
    @ResponseBody
    public List<Blacklist> blacklistByRoom(@RequestParam("Room") String room) {

        List<Blacklist> blacklistByRoom = new ArrayList<>();
        try{
            blacklistByRoom = blacklistRepository.findByRoomId(room);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }


        return blacklistByRoom;
    }


    @CrossOrigin
    @PostMapping(value = "/blacklistByDepartment")
    @ResponseBody
    public List<Blacklist> blacklistByDepartment(@RequestParam("Dep") String dep) {

        List<Blacklist> blacklistByDepartment = new ArrayList<>();
        try{
            blacklistByDepartment = blacklistRepository.blacklistForDepartment(Integer.parseInt(dep));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }


        return blacklistByDepartment;
    }

    @GetMapping("/alerts")
    public List<BlacklistNotification> alerts() {
        return blacklistNotificationRepository.findAll();
    }
    @CrossOrigin
    @GetMapping("/alerts/unseen")
    public List<BlacklistNotification> alertsUnseen() {
        return blacklistNotificationRepository.findBySeen(false);
    }
    @CrossOrigin
    @GetMapping("/alerts/seen")
    public List<BlacklistNotification> alertsSeen() {
        return blacklistNotificationRepository.findBySeen(true);
    }
    @CrossOrigin
    @PostMapping("/alerts/mark_seen")
    public void alertsMarkRead(@RequestBody List<BlacklistNotification> notifications) {
        List<BlacklistNotification> toRemove = new ArrayList<>();
        for (BlacklistNotification notification : notifications)
            for (BlacklistNotification notificationDocument : blacklistNotificationRepository
                .findByEmailAndRoomAndTime(notification.getEmail(), notification.getRoom(), notification.getTime())) {
                    notificationDocument.setSeen(true);
                    blacklistNotificationRepository.save(notificationDocument);
                }

        
        blacklistNotificationRepository.deleteAll( toRemove );
    }
}
