package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.SignalMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VideoCallController {

    @MessageMapping("/signal")
    @SendTo("/topic/call/{roomCode}")
    public SignalMessage handleSignaling(SignalMessage message, @DestinationVariable String roomCode) {
        // Broadcast signaling messages to the room
        return message;
    }

    @GetMapping("/video")
    public String videoCall() {
        return "video";
    }
}
