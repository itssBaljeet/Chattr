package com.noahharris.chattr.controller;

import com.noahharris.chattr.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request, Model model) {
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
        model.addAttribute("users", userService.findAll());
        return "index";
    }

}
