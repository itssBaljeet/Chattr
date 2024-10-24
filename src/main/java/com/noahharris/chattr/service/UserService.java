package com.noahharris.chattr.service;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.model.UserStatus;
import com.noahharris.chattr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Find user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Update user's description
    public void updateUserDescription(String username, String description) {
        User user = findByUsername(username);  // Retrieve user
        user.setDescription(description);  // Set new description
        userRepository.save(user);  // Save updated user
    }

    public ResponseEntity<?> register(UserDTO userDTO) {
        // Persists user object to db
        User user = new User(userDTO.getUsername(),
                passwordEncoder.encode(userDTO.getPassword()),
                userDTO.getEmail(),
                UserStatus.ONLINE);

        // If username exists in db already, respond without saving
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            return ResponseEntity.internalServerError().body("Username is already taken");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            return ResponseEntity.internalServerError().body("Email is already taken");
        } else {
            userRepository.save(user);
            return ResponseEntity.ok().body("User registered successfully");
        }
    }

    public void login(String username) {

        Optional<User> user = userRepository.findByUsername(username).isPresent() ? userRepository.findByUsername(username) : Optional.empty();

        // If id of user not in repository, throw error
        if (user.isEmpty()) {
            return;
        }

        // Update status to online
        user.get().setStatus(UserStatus.ONLINE);
        userRepository.save(user.get());
    }

    public void logout(String username) {
        User user = userRepository.findByUsername(username).isPresent() ? userRepository.findByUsername(username).get() : null;

        if (user == null) {
            return;
        }

        if (userRepository.findById(user.getId()).isEmpty()) {
            return;
        }

        var cUser = userRepository.findById(user.getId()).get();

        cUser.setStatus(UserStatus.OFFLINE);

        userRepository.save(cUser);

    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
