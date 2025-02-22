package com.dietiestates2025.dieti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dietiestates2025.dieti.repositories.MunicipalityRepository;

import java.util.List;


@SpringBootApplication
public class DietiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietiApplication.class, args);
    }

    @Bean
	public CommandLineRunner commandLineRunner(MunicipalityRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<Object[]> results = repo.findAllProvincesOfTorino();
			for (Object[] result : results) {
				String provinceName = (String) result[0];
				System.out.println("Province: " + provinceName);
			}


		};
	}
	/*@Bean
	public CommandLineRunner commandLineRunner(VisitBookingRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<VisitBooking> results = repo.findAll();
			// Print each result in the list
			results.forEach(element -> System.out.println(element));
		};
}*/
}

