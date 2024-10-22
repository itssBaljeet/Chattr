package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.User;
import com.noahharris.chattr.service.CallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CallController {

    // Constructor injection of service class
    private final CallService callService;

    @Autowired
    public CallController(CallService callService) {
        this.callService = callService;
    }

    @PostMapping("/start")
    public Call startCall(User caller, User receiver) {
        return callService.initiateCall(caller, receiver);
    }
}
