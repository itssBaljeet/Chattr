package com.noahharris.chattr;

import com.noahharris.chattr.model.User;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.noahharris.chattr.model.UserStatus.*;

@SpringBootApplication
public class ChattrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChattrApplication.class, args);


	}

	@Bean
	public CommandLineRunner commandLineRunner(
			@Autowired
			UserService userService
	) {
		return args -> {
			// Create mock users
			User user1 = new User("john_doe", "john.doe@example.com", "password123", ONLINE);
			User user2 = new User("jane_smith", "jane.smith@example.com", "securePass!", OFFLINE);

			// Save users to the database
			userService.register(user1);
			userService.register(user2);

			System.out.println("Mock users created!");
		};
	}
}
