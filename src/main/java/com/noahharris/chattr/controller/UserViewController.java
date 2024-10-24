package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserService userService;

    @Autowired
    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        // Move session attributes to the model
        if (session.getAttribute("successMessage") != null) {
            model.addAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage"); // Remove after displaying
        }

        if (session.getAttribute("errorMessage") != null) {
            model.addAttribute("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage"); // Remove after displaying
        }
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();

        // Move session attributes to the model
        if (session.getAttribute("successMessage") != null) {
            model.addAttribute("successMessage", session.getAttribute("successMessage"));
            session.removeAttribute("successMessage"); // Remove after displaying
        }

        if (session.getAttribute("errorMessage") != null) {
            model.addAttribute("errorMessage", session.getAttribute("errorMessage"));
            session.removeAttribute("errorMessage"); // Remove after displaying
        }
        model.addAttribute("userDTO", new UserDTO()); // Create a new UserDTO for the form
        return "register"; // Return the Thymeleaf template name
    }

    // Display user profile
    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        // Retrieve currently authenticated user from security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);
        return "profile";  // Return profile.html template
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        ResponseEntity<?> responseEntity = userService.register(userDTO);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");
            return "redirect:/users/login"; // Redirect to the login page
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + responseEntity.getBody());
            return "redirect:/users/register"; // Redirect back to the registration form with error message
        }
    }

    // Update user description
    @PostMapping("/updateDescription")
    public String updateDescription(@RequestParam("description") String description) {
        // Retrieve current authenticated user's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Update user's description
        userService.updateUserDescription(username, description);

        // Redirect back to the profile page
        return "redirect:/users/profile";
    }
}
