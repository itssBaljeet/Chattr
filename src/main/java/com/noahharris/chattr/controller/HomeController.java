package com.noahharris.chattr.controller;

import com.noahharris.chattr.service.UserService;
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
    public String index(Model model) {
        model.addAttribute("users", userService.findAll());
        return "index";
    }

}
