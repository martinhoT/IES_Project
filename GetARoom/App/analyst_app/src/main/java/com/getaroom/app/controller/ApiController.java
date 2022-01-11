package com.getaroom.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Calendar;
import java.nio.charset.StandardCharsets;

@RestController
public class ApiController {

	private final WebClient apiClient;

	@Autowired
    public ApiController() {
        apiClient = WebClient.create("http://fetcher:8080");
    }

	@GetMapping("/api/today")
	public List<Event> today(@RequestParam(defaultValue = "0", required = false, name = "pageNo") String pageNo, @RequestParam(required = false, name = "room") String room) {
		Integer startIdx, endIdx;
		Integer numElems = 5; // number of events per page 
		List<Event> allEvents = apiTodayRoom(room);
		List<Event> filteredEvents = new ArrayList<>();

		if (pageNo.matches("\\d+")){
			Integer pageNoInt = Integer.parseInt(pageNo);
			// Check list size
			Integer listsize = allEvents.size();
			if (pageNoInt * numElems + numElems < listsize){
				startIdx = pageNoInt * numElems;
				endIdx = startIdx + numElems;
				filteredEvents = allEvents.subList(startIdx, endIdx);
			}else{
				if ((listsize - numElems) < 0){
					startIdx = 0;
				}else{
					startIdx = listsize - numElems;
				}
				endIdx = listsize;
				filteredEvents = allEvents.subList(startIdx, endIdx);
			}
			return filteredEvents;
		}else{
			return allEvents;
		}
    }

	@GetMapping("/api/history")
	public HashMap<Integer, HashMap<Integer, List<Event>>> history(@RequestParam(defaultValue = "") String yfilter, @RequestParam(defaultValue = "") String mfilter, @RequestParam(defaultValue = "") String dfilter) {
		Integer evntMonth, evntYear;

		List<Event> allHistory = apiHistoryYear();
		HashMap<Integer, HashMap<Integer, List<Event>>> history = new HashMap<>();
		
		// Insertion of data
		for (Event event : allHistory){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(event.getTime());
			evntYear = calendar.get(Calendar.YEAR);
			evntMonth = calendar.get(Calendar.MONTH) + 1;

			// Add year if doesnt exist
			if(!history.containsKey(evntYear)){
				HashMap<Integer, List<Event>> month = new HashMap<>();
				history.put(evntYear, month);
			}

			// Add month if doesnt exist
			if(!history.get(evntYear).containsKey(evntMonth)){
				List<Event> day = new ArrayList<>();
				history.get(evntYear).put(evntMonth, day);
			}

			// Add event into history
			history.get(evntYear).get(evntMonth).add(event);
		}

		// Filtering of Data
		// Check if there is a year filter
		if(yfilter.matches("\\d+")){
			HashMap<Integer, HashMap<Integer, List<Event>>> filteredHistory = new HashMap<>();
			Integer yearInt = Integer.parseInt(yfilter);

			// Check if there is a month filter
			if(mfilter.matches("\\d+")){
				Integer monthInt = Integer.parseInt(mfilter);

				// Check if there is a day filter
				if(dfilter.matches("\\d+")){
					Integer dayInt = Integer.parseInt(dfilter);
					List<Event> dayEvents = new ArrayList<>();

					// For each event in that month and year
					for(Event event: history.get(yearInt).get(monthInt)){
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(event.getTime());
						Integer evntDay = calendar.get(Calendar.DAY_OF_MONTH);

						// Check if day is equal
						if(dayInt == evntDay){
							dayEvents.add(event);
						}
					}

					// Add day events to filted
					filteredHistory.put(yearInt, new HashMap<>());
					filteredHistory.get(yearInt).put(monthInt, dayEvents);
					return filteredHistory;
				}else{

					// Just filter with month and year
					filteredHistory.put(yearInt, new HashMap<>());
					filteredHistory.get(yearInt).put(monthInt, history.get(yearInt).get(monthInt));
					return filteredHistory;
				}
			}else{

				// Just filter with year
				filteredHistory.put(yearInt, history.get(yearInt));
				return filteredHistory;
			}
		}
		return history;
    }

	@GetMapping("/api/status")
	public HashMap<String, List<Status>> status() {
		HashMap<String, List<Status>> departments = new HashMap<String, List<Status>>();

		List<Dep> allDepartments = apiGetRequestList("department", Dep.class);

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

	private List<Event> apiTodayRoom(String room) {
		return apiClient.get()
			.uri(uriBuilder -> uriBuilder
			.path("/api/today")
			.queryParam("room", room)
			.build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToFlux( response -> response.bodyToFlux(Event.class) )
			.collectList().block();
	}

	private List<Event> apiHistoryYear() {
		return apiClient.get()
			.uri(uriBuilder -> uriBuilder
			.path("/api/today_history")
			.build())
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.acceptCharset(StandardCharsets.UTF_8)
			.exchangeToFlux( response -> response.bodyToFlux(Event.class) )
			.collectList().block();
	}
}
