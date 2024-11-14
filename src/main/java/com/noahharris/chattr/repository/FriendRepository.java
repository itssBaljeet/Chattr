package com.noahharris.chattr.repository;

import com.noahharris.chattr.model.Friend;
import com.noahharris.chattr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findBySender(User user);
    List<Friend> findByReceiver(User user);
    List<Friend> findByReceiverAndStatus(User user, Friend.FriendshipStatus status);
    List<Friend> findBySenderAndStatus(User user, Friend.FriendshipStatus status);
    @Query("SELECT f FROM Friend f WHERE (f.sender = :user OR f.receiver = :user) AND f.status = :status")
    List<Friend> findFriendsByUserAndStatus(@Param("user") User user, @Param("status") Friend.FriendshipStatus status);
    List<Friend> findBySenderOrReceiverAndStatus(User sender, User receiver, Friend.FriendshipStatus status);
    Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
}
