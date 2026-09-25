package com.example.demo;

import com.example.demo.model.SequenceAllocator;
import com.example.demo.repository.SequenceRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeUrlSequence(SequenceRepository sequenceRepository) {
		return args -> sequenceRepository.findById("url_seq")
				.orElseGet(() -> sequenceRepository.save(new SequenceAllocator("url_seq", 1)));
	}

}
