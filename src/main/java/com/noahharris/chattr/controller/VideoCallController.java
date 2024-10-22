package com.noahharris.chattr.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class VideoCallController {

    @MessageMapping("/call")
    @SendTo("/topic/video")
    public String initiateCall(String message) throws Exception {
        return message;
    }
}
