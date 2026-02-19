package dnd.manager.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import dnd.manager.app.model.Characters;
import dnd.manager.app.model.SpecieCatalog;
import dnd.manager.app.repository.CharactersRepository;
import dnd.manager.app.repository.SpecieCatalogRepository;

@SpringBootApplication
public class AppApplication implements CommandLineRunner{


	@Autowired
	private CharactersRepository charactersRepository;

	@Autowired
	private SpecieCatalogRepository specieCatalogRepository;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Aplicación iniciada correctamente en http://localhost:8080");
		
		System.out.println("Personajes creados:");
		List<Characters> characters = charactersRepository.findAll();
		characters.stream().forEach(System.out::println);

		System.out.println("Especies en el catálogo:");
		List<SpecieCatalog> species = specieCatalogRepository.findAll();
		species.stream().forEach(System.out::println);
	}


}
