package com.example.join.controller;

import com.example.join.entity.Profile;
import com.example.join.service.ProfileService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}
	
	@GetMapping("/profile")
	public String profile(Model model) {
		Profile profile = profileService.getProfile(1L);
		model.addAttribute("profile", profile);
		model.addAttribute("boards", profile.getFoodBoards());
		return "profile";
	}
	
	@GetMapping("/profile/edit")
	public String edit(Model model) {
		Profile profile = profileService.getProfile(1L);
		model.addAttribute("profile", profile);
		return "profile_edit";
	}

	@PostMapping("/profile/edit")
	public String update(Profile profile) {
		profileService.update(profile);
		return "redirect:/profile";
	}
}