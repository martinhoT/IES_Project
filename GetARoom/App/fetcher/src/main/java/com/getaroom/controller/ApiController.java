package com.getaroom.controller;

import java.util.List;
import java.util.Map;

import com.getaroom.entity.Dep;
import com.getaroom.entity.Event;
import com.getaroom.entity.Room;
import com.getaroom.repository.StatusRepository;
import com.getaroom.repository.TodayRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ApiController {
    
    private final StatusRepository statusRepository;
    private final TodayRepository todayRepository;

    @Autowired
    public ApiController(StatusRepository statusRepository, TodayRepository todayRepository) {
        this.statusRepository = statusRepository;
        this.todayRepository = todayRepository;
    }

    @GetMapping("/api/today")
    public List<Event> today(@RequestParam(required = false) String room) {
        /**
         * TODO
         */
        return null;
    }

    @GetMapping("/api/history")
    public Map<String, List<Event>> history(@RequestParam(required = false) String year) {
        /**
         * TODO
         */
        return null;
    }

    @GetMapping("/api/status")
    public Map<String, List<Room>> status(@RequestParam(required = false) int dep) {
        /**
         * TODO
         */
        return null;
    }

    @GetMapping("/api/department")
    public List<Dep> department() {
        return statusRepository.findAllDep();
    }

}
