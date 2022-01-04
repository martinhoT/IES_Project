package com.getaroom.app.controller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.History;
import com.getaroom.app.entity.Today;
import com.getaroom.app.entity.Room;
import com.getaroom.app.entity.RoomStyle;
import com.getaroom.app.repository.HistoryRepository;
import com.getaroom.app.repository.RoomStyleRepository;
import com.getaroom.app.repository.StatusRepository;
import com.getaroom.app.repository.TodayRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private final StatusRepository statusRepository;
    private final TodayRepository todayRepository;
    private final HistoryRepository historyRepository;
    private final RoomStyleRepository roomStyleRepository;

    @Autowired
    public ApiController(StatusRepository statusRepository, TodayRepository todayRepository, RoomStyleRepository roomStyleRepository, HistoryRepository historyRepository) {
        this.statusRepository = statusRepository;
        this.todayRepository = todayRepository;
        this.roomStyleRepository = roomStyleRepository;
        this.historyRepository = historyRepository;

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

    @CrossOrigin
    @GetMapping("/today")
    public List<Today> today(@RequestParam(defaultValue = "") String room) {
        if (room.isEmpty()){
            return todayRepository.findAll();
        }else{
            return todayRepository.findByRoom(room);
        }
    }

    @GetMapping("/history")
    public List<History> history() {
        List<History> allHistory = historyRepository.findAll();
        return allHistory;
    }

    @GetMapping("/status")
    public Map<String, List<Room>> status(@RequestParam(required = false) String dep) {
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

}
