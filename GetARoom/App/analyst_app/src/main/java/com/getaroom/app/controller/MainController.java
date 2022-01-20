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


@RequestMapping("")
@Controller
public class MainController {

	private final WebClient apiClient;

	@Autowired
	public MainController() {
		apiClient = WebClient.create("http://fetcher:8080");
	}

	@GetMapping("/")
	public String entryPoint(User user) {
		return "redirect:/login";
	}
	
	@GetMapping("/api")
	public String api() {
		return "api";
	}

	@GetMapping("/graphs")
	public String graphs(Model model) {
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