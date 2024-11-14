package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.Call;
import com.noahharris.chattr.model.CallRequestMessage;
import com.noahharris.chattr.model.CallResponseMessage;
import com.noahharris.chattr.model.User;
import com.noahharris.chattr.service.CallService;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class CallController {

    // Constructor injection of service class
    private final CallService callService;
    private final UserService userService;
    private final SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    public CallController(CallService callService, UserService userService, SimpMessagingTemplate brokerMessagingTemplate) {
        this.callService = callService;
        this.userService = userService;
        this.brokerMessagingTemplate = brokerMessagingTemplate;
    }

    @PostMapping("/start")
    public Call startCall(User caller, User receiver) {
        return callService.initiateCall(caller, receiver);
    }

    @PostMapping("/end/{id}")
    public void endCall(@PathVariable Long id) {
        Call call = callService.findById(id); // Fetch call using the provided ID
        callService.endCall(call);
    }

    @MessageMapping("/call/request")
    public void handleCallRequest(CallRequestMessage requestMessage, Principal caller) {
        User callerUser = userService.findByUsername(caller.getName());
        User receiverUser = userService.findByUsername(requestMessage.getReceiverUsername());

        Call call = callService.initiateCall(callerUser, receiverUser);
        requestMessage.setCallId(call.getId());

        brokerMessagingTemplate.convertAndSendToUser(receiverUser.getUsername(), "/queue/callNotifications", requestMessage);
    }

    @MessageMapping("/call/response")
    public void handleCallResponse(CallResponseMessage responseMessage, Principal receiver) {
        Call call = callService.findById(responseMessage.getCallId());

        if (responseMessage.getResponse() == CallResponseMessage.ResponseType.ACCEPT) {
            call.setStatus(Call.CallStatus.ACTIVE);
            callService.save(call);

            // Notify both caller and receiver
            brokerMessagingTemplate.convertAndSendToUser(
                    call.getCaller().getUsername(), "/queue/callNotifications", responseMessage);
            brokerMessagingTemplate.convertAndSendToUser(
                    call.getReceiver().getUsername(), "/queue/callNotifications", responseMessage);
        } else {
            // Set call as declined or remove if desired
            call.setStatus(Call.CallStatus.ENDED);
            callService.save(call);
        }
    }

}
