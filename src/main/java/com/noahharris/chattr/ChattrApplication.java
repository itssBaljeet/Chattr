package com.noahharris.chattr;

import com.noahharris.chattr.model.UserDTO;
import com.noahharris.chattr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
			UserDTO user1 = new UserDTO("Dark_void", "123", "dark.void@gmail.com");
			UserDTO user2 = new UserDTO("Light_void", "456", "light.void@gmail.com");

			// Save users to the database and prints the result to console
			System.out.println(userService.register(user1).getBody().toString());
			System.out.println(userService.register(user2).getBody().toString());

			System.out.println("Mock users creation attempted!");
		};
	}
}
