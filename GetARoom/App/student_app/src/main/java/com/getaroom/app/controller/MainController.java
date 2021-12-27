package com.getaroom.app.controller;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Status;
import com.getaroom.app.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("")
@Controller
public class MainController {

	private final WebClient apiClient;

	@Autowired
	public MainController(){
		apiClient = WebClient.create("http://fetcher:8080");
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}

	@GetMapping(value="/studyRooms")
	public String getAllDepartments(Model model) {

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		Collections.sort(allDepartments, Comparator.comparingInt(Dep::getDepNumber));

		model.addAttribute("depList", allDepartments);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){

		List<Status> allRooms = apiStatusDep(dep).get(dep);

		ModelAndView mav = new ModelAndView();

		Collections.sort(allRooms, Comparator.comparingDouble(Status::getOccupacyNumber));

		mav.addObject("rooms", allRooms.stream().limit(10).collect(Collectors.toList()));
		mav.setViewName("suggested_room");

		return mav;
	}
	
	@GetMapping("/error")
	public String error() {
		return "error";
	}

	/**
	 * GET HTTP request to the API located in the fetcher instance.
	 * This version returns a list of results.
	 * 
	 * @param <E>			Generic type representing the class of the objects in the list. Should be the same as the class passed as argument
	 * @param uriAppend		The final location specification on the API. It will essentially be appended to the uri "http://localhost:8080/api/"
	 * @param elementClass	The class of the elements in the list
	 * @return				The list of objects returned by the API call
	 */
	private <E> List<E> apiGetRequestList(String uriAppend, Class<E> elementClass) {
		return apiClient.get()
			.uri("/api/" + uriAppend)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToFlux( response -> response.bodyToFlux(elementClass) )
			.collectList().block();
	}

	// TODO: is there a better way?
	@SuppressWarnings("unchecked")
	private Map<String, List<Status>> apiStatusDep(String dep) {
		return apiClient.get()
			.uri("/api/status")
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.attribute("dep", dep)
			.exchangeToMono(response -> {
				return response.bodyToMono(Map.class);
			})
			.block();
	}

}

