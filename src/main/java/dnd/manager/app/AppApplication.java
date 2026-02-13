package dnd.manager.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dnd.manager.app.model.Characters;
import dnd.manager.app.repository.CharacterRepository;

@SpringBootApplication
public class AppApplication implements CommandLineRunner{


	@Autowired
	private CharacterRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Aplicación iniciada correctamente en http://localhost:8080");
		List<Characters> characters = repository.findAll();
		characters.stream().forEach(System.out::println);
	}


}
