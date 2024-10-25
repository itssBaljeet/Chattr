package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserApiController {

    @GetMapping("/current-username")
    public String getCurrentUsername(Principal principal) {
        return principal.getName();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
