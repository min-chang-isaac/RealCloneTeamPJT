package com.example.join.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String home(Model model) {
        model.addAttribute("message", "Hello Thymeleaf!");
        return "profile"; // templates/home.html
    }
    @GetMapping("/image")
    public String getProfileImage() {
    	return "/images/profile.png";
    }
    
    
}

