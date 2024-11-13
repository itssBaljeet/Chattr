package com.noahharris.chattr.controller;

import com.noahharris.chattr.model.Friend;
import com.noahharris.chattr.model.User;
import com.noahharris.chattr.service.FriendService;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @Autowired
    public FriendController(FriendService friendService, UserService userService) {
        this.friendService = friendService;
        this.userService = userService;
    }

    @PostMapping("/send-request")
    public String sendRequest(@RequestParam String username) {
        User currentUser = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        User friend = userService.findByUsername(username);

        if (friend != null && !friend.equals(currentUser)) {
            friendService.sendFriendRequest(currentUser, friend);
        }
        return "redirect:/users/friends/friends-list";
    }

    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam Long friendId) {
        friendService.acceptFriendRequest(friendId);
        return "redirect:/users/friends/friends-list";
    }

    @PostMapping("/reject")
    public String rejectFriendRequest(@RequestParam Long friendId) {
        friendService.rejectFriendRequest(friendId);
        return "redirect:/users/friends/friends-list";
    }

    @PostMapping("/block")
    public String blockFriendRequest(@RequestParam Long friendId) {
        friendService.blockUser(friendId);
        return "redirect:/users/friends/friends-list";
    }

    @GetMapping("/friends-list")
    public String friends(Model model) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(user.getUsername());
        List<Friend> friends = friendService.getConfirmedFriends(user);
        List<Friend> friendRequests = friendService.getPendingFriends(user);

        model.addAttribute("friends", friends);
        model.addAttribute("friendRequests", friendRequests);

        return "friends";
    }
}
