package com.negotiatex.controller;

import com.negotiatex.dto.request.LoginRequest;
import com.negotiatex.dto.request.RegisterRequest;
import com.negotiatex.repository.UserRepository;
import com.negotiatex.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = "/register")
	public String register(@ModelAttribute RegisterRequest request, HttpServletResponse response) {
		try {
			authService.register(request);
			// Optional: Auto-login after successful registration
			String token = authService.authenticate(new LoginRequest(request.getEmail(), request.getPassword()));
			addJwtCookie(response, token);
			return determineRedirect(request.getEmail());
		} catch (Exception e) {
			return "redirect:/register?error=" + e.getMessage();
		}
	}

	@PostMapping(value = "/login")
	public String login(@ModelAttribute LoginRequest request, HttpServletResponse response) {
		try {
			String token = authService.authenticate(request);
			addJwtCookie(response, token);
			return determineRedirect(request.getEmail());
		} catch (Exception e) {
			return "redirect:/login?error=true";
		}
	}

	private String determineRedirect(String email) {
		
		com.negotiatex.entity.User user = userRepository.findByEmail(email).orElse(null);
		
		if (user != null && user.getRole() == com.negotiatex.entity.Role.SELLER) {
			return "redirect:/seller/dashboard";
		}
		
		return "redirect:/catalog";
	}

	// Explicitly mapping logout
	@PostMapping(value = "/logout")
	public String logout(HttpServletResponse response) {

		Cookie cookie = new Cookie("JWT", "");
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0); // Delete cookie
		response.addCookie(cookie);
		
		return "redirect:/login?logout=true";
	}

	private void addJwtCookie(HttpServletResponse response, String token) {
		
		Cookie cookie = new Cookie("JWT", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false); // Enable this in production with HTTPS
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60); // 1 day
		response.addCookie(cookie);
	}
}
