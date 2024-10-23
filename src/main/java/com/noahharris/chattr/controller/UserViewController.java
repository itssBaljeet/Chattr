package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserViewController {

    private final UserService userService;

    @Autowired
    public UserViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO()); // Create a new UserDTO for the form
        return "register"; // Return the Thymeleaf template name
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("userDTO") UserDTO userDTO, RedirectAttributes redirectAttributes) {
        Optional<User> user = userService.login(userDTO.getUsername(), userDTO.getPassword());

        if (user.isPresent()) {
            redirectAttributes.addFlashAttribute("successMessage", "Login successful!");
            return "redirect:/index";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid username or password!");
            return "redirect:/users/login";
        }
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

}
