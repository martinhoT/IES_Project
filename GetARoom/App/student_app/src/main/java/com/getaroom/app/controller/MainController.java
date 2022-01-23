package com.getaroom.app.controller;

import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Room;
import com.getaroom.app.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("")
@Controller
public class MainController {

	private final WebClient apiClient;
	
	// Get cookies
	public Boolean VerifyCookie(HttpServletRequest request, String name){
		Cookie[] cookies = request.getCookies();
		if(cookies == null) return false;
		for(Cookie cookie : cookies){
			if(cookie.getName().equals(name) && cookie.getValue().equals("secret1")) return true;
		};
		return false;
	}
	  
	@Autowired
	public MainController(){
		apiClient = WebClient.create("http://fetcher:8080");
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}

	@GetMapping(value="/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		// See if we are logged in or not
        if(VerifyCookie(request, "user-id")){
			Cookie jwtTokenCookie = new Cookie("user-id", "null");

			jwtTokenCookie.setMaxAge(0);
			jwtTokenCookie.setSecure(false);
			jwtTokenCookie.setHttpOnly(true);
	
			// Set cookie onto user
			response.addCookie(jwtTokenCookie);
			return "redirect:/login";
        }else{
			return "error";
		}
	}

	@GetMapping(value="/studyRooms")
	public String getAllDepartments(Model model, HttpServletRequest request) {

		// See if we are logged in or not
		if(!VerifyCookie(request, "user-id")){
			return "redirect:/login";
		}

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		Collections.sort(allDepartments, Comparator.comparingInt(Dep::getId));

		model.addAttribute("depList", allDepartments);
		return "search_room";
	}

	@PostMapping(value = "/studyRooms")
	@ResponseBody
	public ModelAndView getStudyRooms(Model model, @RequestParam("npeople") int npeople, @RequestParam("dep") String dep){

		List<Room> allRooms = apiRoomByDep(dep);

		ModelAndView mav = new ModelAndView();

		Collections.sort(allRooms, Comparator.comparingDouble(Room::getOccupancy));

		List<Room> result = new ArrayList<>();
		for (Room r : allRooms){
			if (r.getOccupancy() * r.getMaxNumberOfPeople() + npeople <= r.getMaxNumberOfPeople())
				result.add(r);
		}

		mav.addObject("rooms", result.stream().limit(10).collect(Collectors.toList()));

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

	private List<Room> apiRoomByDep(String dep) {
		String json = apiClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/room")
				.queryParam("dep", dep)
				.build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToMono(response -> response.bodyToMono(String.class))
			.block();

		Gson gson = new Gson();
		List<Room> res = new ArrayList<>();
		for (JsonElement elem : gson.fromJson(json, JsonObject.class).getAsJsonArray(dep))
			res.add(gson.fromJson(elem.toString(), Room.class));
		return res;
	}

}

