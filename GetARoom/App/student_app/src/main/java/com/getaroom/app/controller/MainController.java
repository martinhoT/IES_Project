package com.getaroom.app.controller;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Status;
import com.getaroom.app.entity.User;
import com.getaroom.app.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.servlet.ModelAndView;

import ch.qos.logback.core.joran.conditional.ElseAction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping("")
@Controller
public class MainController {

	private final RoomRepository roomRepository;
	private final WebClient apiClient;

	@Autowired
	public MainController(RoomRepository roomRepository){
		this.roomRepository = roomRepository;
		apiClient = WebClient.create("http://fetcher:8080");
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}

	@GetMapping(value="/studyRooms")
	public String getAllDepartments(Model model) {

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		Collections.sort(allDepartments, (o1,o2) -> o1.getdep().compareTo(o2.getdep()));

		model.addAttribute("depList", allDepartments);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){

		// List<Status> allRooms = roomRepository.findAllRooms(dep);
		// List<Status> allRooms = apiGetRequestList("status");

		ModelAndView mav = new ModelAndView();

		Collections.sort(allRooms, (o1,o2) -> o1.getOccupacy().compareTo(o2.getOccupacy()));

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

}

