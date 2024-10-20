package com.noahharris.chattr.service;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(User user) {
        userRepository.save(user);
    }

}
