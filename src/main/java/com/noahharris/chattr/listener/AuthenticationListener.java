package com.noahharris.chattr.listener;

import com.noahharris.chattr.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationListener {

    private final UserService userService;

    public AuthenticationListener(UserService userService) {
        this.userService = userService;
    }

    // Listen to successful login event
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();
        userService.login(username); // Update user status to ONLINE
    }
}
