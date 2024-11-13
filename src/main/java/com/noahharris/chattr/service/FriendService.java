package com.noahharris.chattr.service;

import com.noahharris.chattr.model.Friend;
import com.noahharris.chattr.model.User;
import com.noahharris.chattr.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public boolean updateFriendship(Long friendId, Friend.FriendshipStatus status) {
        Optional<Friend> friendship = friendRepository.findById(friendId);

        if (friendship.isPresent()) {
            friendship.get().setStatus(status);
            friendRepository.save(friendship.get());
            return true;
        }

        return false;
    }

    public boolean sendFriendRequest(User user1, User user2) {
        Optional<Friend> friendship = friendRepository.findByUserAndFriend(user1, user2);

        if (friendship.isPresent()) {
            return false;
        }

        friendRepository.save(new Friend(user1, user2, Friend.FriendshipStatus.PENDING));
        return true;
    }

    public List<Friend> getConfirmedFriends(User user) {
        return friendRepository.findByUserAndStatus(user, Friend.FriendshipStatus.ACCEPTED);
    }

    public List<Friend> getPendingFriends(User user) {
        return friendRepository.findByUserAndStatus(user, Friend.FriendshipStatus.PENDING);
    }

    public boolean acceptFriendRequest(Long friendId) {
        Optional<Friend> friendship = friendRepository.findById(friendId);

        if (friendship.isPresent()) {
            friendship.get().setStatus(Friend.FriendshipStatus.ACCEPTED);
            friendRepository.save(friendship.get());
            return true;
        }

        return false;
    }

    public boolean rejectFriendRequest(Long friendId) {
        Optional<Friend> friendship = friendRepository.findById(friendId);

        if (friendship.isPresent()) {
            friendship.get().setStatus(Friend.FriendshipStatus.REJECTED);
            friendRepository.save(friendship.get());
            return true;
        }

        return false;
    }

    public boolean blockUser(Long friendId) {
        Optional<Friend> friendship = friendRepository.findById(friendId);

        if (friendship.isPresent()) {
            friendship.get().setStatus(Friend.FriendshipStatus.BLOCKED);
            friendRepository.save(friendship.get());
            return true;
        }

        return false;
    }
}
