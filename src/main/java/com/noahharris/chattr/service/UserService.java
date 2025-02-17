package com.noahharris.chattr.service;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), new ArrayList<>());
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
                User.UserStatus.ONLINE);

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

        if (user.isEmpty()) {
            return;
        }

        // Update status to online
        user.get().setStatus(User.UserStatus.ONLINE);
        userRepository.save(user.get());
    }

    public void logout(String username) {
        Optional<User> user = userRepository.findByUsername(username).isPresent() ? userRepository.findByUsername(username) : Optional.empty();

        if (user.isEmpty()) {
            return;
        }

        // Update status to offline
        user.get().setStatus(User.UserStatus.OFFLINE);
        userRepository.save(user.get());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
