package com.getaroom.app.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.getaroom.app.entity.Status;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.getaroom.app.entity.Dep;
import com.getaroom.app.entity.Event;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.nio.charset.StandardCharsets;

@RestController
public class ApiController {

	private final WebClient apiClient;

    public ApiController() {
        apiClient = WebClient.create("http://fetcher:8080");
    }

	public static List<Event> getToday(String StatusName){
		List<Event> eventList = new ArrayList<Event>();
		return eventList;
	}

	public static HashMap<String, List<Event>> getHistory(String year){
		HashMap<String, List<Event>> history = new HashMap<String, List<Event>>();
		return history;
	}

	public static HashMap<String, List<Status>> getStatus(){
		HashMap<String, List<Status>> departments = new HashMap<String, List<Status>>();

		
		return departments;
	}

	@GetMapping("/api/today")
	public List<Event> today(@RequestParam(required = false) String Status) {
		return getToday(Status);
    }

	@GetMapping("/api/history")
	public HashMap<String, List<Event>> history(@RequestParam(required = false) String year) {
		return getHistory(year);
    }

	@GetMapping("/api/status")
	public HashMap<String, List<Status>> status() {
		HashMap<String, List<Status>> departments = new HashMap<String, List<Status>>();

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

		Collections.sort(allDepartments, Comparator.comparingInt(Dep::getDepNumber));

		for(Dep dep: allDepartments){
			String department = dep.getDep();
			departments.put("Department " + department, apiStatusDep(department));
		}
		return departments;
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

	private List<Status> apiStatusDep(String dep) {
		String json = apiClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/api/status")
				.queryParam("dep", dep)
				.build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToMono(response -> response.bodyToMono(String.class))
			.block();

		Gson gson = new Gson();
		List<Status> res = new ArrayList<>();
		for (JsonElement elem : gson.fromJson(json, JsonObject.class).getAsJsonArray(dep))
			res.add(gson.fromJson(elem.toString(), Status.class));
		return res;
	}
}
