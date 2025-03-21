package com.dietiestates2025.dieti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;


@SpringBootApplication
public class DietiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietiApplication.class, args);
    }

    /*@Bean
	public CommandLineRunner commandLineRunner(BuyingAndSellingRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<Object[]> results = repo.findAllPriceAndStreet();
			for (Object[] result : results) {
				BigDecimal street = (BigDecimal) result[0];
				String zipCode = (String) result[1];
				System.out.println("Street: " + street + " zipcode " + zipCode);
			}


		};
	} 
		*/
	@Bean
	public CommandLineRunner commandLineRunner(AddressRepository addressRepository, MunicipalityRepository municipalityRepository) {
		return args -> {
			Optional<Address> o = addressRepository.findByStreetAndHouseNumberAndMunicipality("Piazza Milano",34,municipalityRepository.findById("28064").get());
			System.out.println(o.get().getStreet());
		};
	}

	/* 
	@Autowired
    private ControllerFactory controllerFactory;
	@Autowired
	private UserService userService;
	@Bean
	public CommandLineRunner commandLineRunner(RegionRepository repo) {
		return args -> {
			User u = userService.getUserByMail("mario@gmail.com");
			AbstractRoleController abstractRoleController = u.getController();
			abstractRoleController.print();
		};
	}

	*/
}