package com.dietiestates2025.dieti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;

import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.controller.AbstractRoleController;
import com.dietiestates2025.dieti.controller.AgentController;
import com.dietiestates2025.dieti.factory.ControllerFactory;
import com.dietiestates2025.dieti.model.Role;
import com.dietiestates2025.dieti.model.Service;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.RegionRepository;


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
	public CommandLineRunner commandLineRunner(PropertyRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<String> results = repo.findAllServiceOfProperty(1);
			// Print each result in the list
			for (String result : results) {
				System.out.println("id: " + result);
			}
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