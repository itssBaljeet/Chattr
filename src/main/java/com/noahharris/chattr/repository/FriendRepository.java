package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Friend;
import com.noahharris.chattr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByFriendId(Long id);
    List<Friend> findByFriendName(String name);
    List<Friend> findByUser(User user);
    List<Friend> findByFriend(User user);
}
