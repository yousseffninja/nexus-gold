package com.rocketeers.nexus_gold;

import com.rocketeers.nexus_gold.enums.Roles;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class NexusGoldApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(NexusGoldApplication.class, args);
	}

	public void run (String... args) {
		User adminAccount = userRepository.findByRole(Roles.ADMIN);
		if (adminAccount == null) {

			User user = new User();

			user.setEmail("admin@gmail.com");
			user.setFirstName("Admin");
			user.setLastName("Admin");
			user.setRole(Roles.ADMIN);
			user.setDisplayName("Admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));

			userRepository.save(user);

		}
	}

}
