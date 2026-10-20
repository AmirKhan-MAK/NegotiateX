package com.negotiatex.controller;

import com.negotiatex.entity.Role;
import com.negotiatex.entity.User;
import com.negotiatex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", principal.getName());
            
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("currentUserId", user.getId());
                model.addAttribute("userRole", user.getRole().name());
                model.addAttribute("isSeller", user.getRole() == Role.SELLER);
                model.addAttribute("isBuyer", user.getRole() == Role.BUYER);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("isSeller", false);
            model.addAttribute("isBuyer", false);
        }
    }
}
