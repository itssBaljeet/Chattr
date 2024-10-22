package com.noahharris.chattr.service;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserStatus;
import com.noahharris.chattr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(User user) {
        // Persists user object to db
        user.setStatus(UserStatus.ONLINE);
        userRepository.save(user);
    }

    public User login(User user) {

        // If id of user not in repository, throw error
        if (userRepository.findById((user.getId())).isEmpty()) {
            throw new RuntimeException("User not found");
        }

        // Current user fetched from repository using id from parameter
        var cUser = userRepository.findById(user.getId()).get();

        // Check if password in parameter object matches password
        // from user object fetched from db
        if (!cUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Password incorrect");
        }

        // Update status to online
        cUser.setStatus(UserStatus.ONLINE);

        return cUser;
    }

    public void logout(User user) {

        if (userRepository.findById(user.getId()).isEmpty()) {
            throw new RuntimeException("User not found");
        }

        var cUser = userRepository.findById(user.getId()).get();

        cUser.setStatus(UserStatus.OFFLINE);

        userRepository.save(cUser);

    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
