package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Friend;
import com.noahharris.chattr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUser(User user);
    List<Friend> findByUserAndStatus(User user, Friend.FriendshipStatus status);
    Optional<Friend> findByUserAndFriend(User user, User friend);
}
