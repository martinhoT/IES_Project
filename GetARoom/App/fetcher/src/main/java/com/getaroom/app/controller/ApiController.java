package com.getaroom.app.controller;

import java.util.List;
import java.util.Map;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;
import com.getaroom.app.entity.Room;
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

    @Autowired
    public ApiController(StatusRepository statusRepository, TodayRepository todayRepository) {
        this.statusRepository = statusRepository;
        this.todayRepository = todayRepository;
    }

    @GetMapping("/today")
    public List<Event> today(@RequestParam(required = false) String room) {
        /**
         * TODO
         */
        return null;
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
        /**
         * TODO
         */
        
        // Used by student_app
        if (dep != null)
            return Map.of(dep, statusRepository.findAllRooms(dep));

        return null;
    }

    @GetMapping("/department")
    public List<Dep> department() {
        return statusRepository.findAllDep();
    }

}
