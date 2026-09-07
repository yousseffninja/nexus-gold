package com.rocketeers.nexus_gold;

import com.rocketeers.nexus_gold.model.Role;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.RoleRepository;
import com.rocketeers.nexus_gold.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableAsync
public class NexusGoldApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(NexusGoldApplication.class, args);
	}

	public void run (String... args) {
		Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
		if (adminRole == null) {
			adminRole = Role.builder().name("ADMIN").build();
			roleRepository.save(adminRole);
		}

		List<User> adminAccounts = userRepository.findByRole(adminRole);
		if (adminAccounts.isEmpty()) {

			User user = new User();

			user.setEmail("admin@gmail.com");
			user.setFirstName("Admin");
			user.setLastName("Admin");
			user.setRole(adminRole);
			user.setDisplayName("Admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));

			userRepository.save(user);

		}
	}

}
