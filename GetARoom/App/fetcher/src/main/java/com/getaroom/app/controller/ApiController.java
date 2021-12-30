package com.getaroom.app.controller;

import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;
import com.getaroom.app.entity.Room;
import com.getaroom.app.entity.RoomStyle;
import com.getaroom.app.repository.RoomStyleRepository;
import com.getaroom.app.repository.StatusRepository;
import com.getaroom.app.repository.TodayRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final StatusRepository statusRepository;
    private final TodayRepository todayRepository;
    private final RoomStyleRepository roomStyleRepository;

    @Autowired
    public ApiController(StatusRepository statusRepository, TodayRepository todayRepository, RoomStyleRepository roomStyleRepository) {
        this.statusRepository = statusRepository;
        this.todayRepository = todayRepository;
        this.roomStyleRepository = roomStyleRepository;

        if (roomStyleRepository.count() == 0) {
            // TODO: from static JSON file, populate database
        }
    }

    @GetMapping("/today")
    public List<Event> today(@RequestParam(defaultValue = "") String room) {
        if (room.isEmpty()){
            return todayRepository.findAll();
        }else{
            return todayRepository.findByRoom(room);
        }
    }

    @GetMapping("/history")
    public Map<String, List<Event>> history(@RequestParam(required = false) String year) {
        /**
         * TODO
         */
        return null;
    }

    @GetMapping("/status")
    public Map<String, List<Room>> status(@RequestParam(required = false) String dep) {
        // Used by student_app and analyst_app
        if (dep != null)
            return Map.of(dep, statusRepository.findAllRooms(dep));

        return null;
    }

    @GetMapping("/department")
    public List<Dep> department() {
        return statusRepository.findAllDep();
    }

    // Used in heatmaps
    @GetMapping("/roomStyles")
    public List<RoomStyle> roomStyles(@RequestParam(required = false) String dep) {
        return roomStyleRepository.findAllRooms(dep);
    }

}
