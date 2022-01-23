package com.getaroom.app.controller;

import java.nio.charset.StandardCharsets;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.getaroom.app.entity.User;
import com.getaroom.app.other.LoginData;
import com.getaroom.app.other.RegisterData;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class LoginController {

    private final WebClient apiClient;

    // setCookie to user
    public void setCookie(String name, HttpServletResponse response) {
        // Create cookie
        Cookie jwtTokenCookie = new Cookie("user-id", "secret1");

        jwtTokenCookie.setMaxAge(86400);
        jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);

        // Set cookie onto user
        response.addCookie(jwtTokenCookie);
    }

    public LoginController() {
        apiClient = WebClient.create("http://fetcher:8080");
    }

    @GetMapping("/login")
    public ModelAndView showLoginForm(User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login_form");
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView showRegisterForm(User user) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register_form");
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid User user, BindingResult result, Model model, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors() && result.getAllErrors().size() > 2)  {
            modelAndView.setViewName("login_form");
            return modelAndView;
        }

        if (apiAuthPost("login", new LoginData(user.getName(), user.getPassword()), LoginData.class)){
            setCookie(user.getName(), response);
            modelAndView.setViewName("redirect:/studyRooms");
        }
        else{
            modelAndView.setViewName("login_form");
            model.addAttribute("wrong", true);
        }
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid User user, BindingResult result, Model model, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        if (result.hasErrors()) {
            modelAndView.setViewName("register_form");
            return modelAndView;
        }

        if (apiAuthPost("register", new RegisterData(user.getName(), user.getPassword(), user.getEmail()), RegisterData.class)){
            setCookie(user.getName(), response);
            modelAndView.setViewName("redirect:/studyRooms");
        }
        else{
            modelAndView.setViewName("register_form");
            model.addAttribute("wrong", true);
        }
        return modelAndView;
    }

    /**
     * POST HTTP request to the API located in the fetcher instance.
     * This version tries to authenticate a user or register them.
     *
     * @param <E>			Generic type representing the class of the object in the body. Should be the same as the class passed as argument
     * @param uriAppend		The final location specification on the API. It will essentially be appended to the uri "http://localhost:8080/api/auth/"
     * @param elementClass	The class of the elements in the body
     * @return				A boolean value indicating whether the request was successful or not
     */
    private <T> Boolean apiAuthPost(String uriAppend, T body, Class<T> bodyClass) {
        return apiClient.post()
                .uri("api/auth/" + uriAppend)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(Mono.just(body), bodyClass)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }
}