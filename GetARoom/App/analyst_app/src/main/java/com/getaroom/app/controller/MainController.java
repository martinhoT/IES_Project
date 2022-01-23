package com.getaroom.app.controller;

import java.nio.charset.StandardCharsets;

import com.getaroom.app.entity.User;
import com.getaroom.app.entity.Status_event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

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
			if(cookie.getName().equals(name) && cookie.getValue().equals("secret3")) return true;
		};
		return false;
	}

	@Autowired
	public MainController() {
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
			jwtTokenCookie.setSecure(true);
			jwtTokenCookie.setHttpOnly(true);
	
			// Set cookie onto user
			response.addCookie(jwtTokenCookie);
			return "redirect:/login";
        }else{
			return "error";
		}
	}

	@GetMapping("/api")
	public String api(HttpServletRequest request) {

		// See if we are logged in or not
		if(!VerifyCookie(request, "user-id")){
			return "redirect:/login";
		}

		return "api";
	}

	@GetMapping("/graphs")
	public String graphs(Model model, HttpServletRequest request) {

		// See if we are logged in or not
		if(!VerifyCookie(request, "user-id")){
			return "redirect:/login";
		}
		
		List<Status_event> Status_Events = apiGetRequestList("status", Status_event.class);
		model.addAllAttributes(Map.of(
			"status_events", Status_Events
		));
		return "graphs";
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
}