package com.dietiestates2025.dieti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Dashboard;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Municipality;
import com.dietiestates2025.dieti.model.Offer;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.Role;
import com.dietiestates2025.dieti.model.Sale;
import com.dietiestates2025.dieti.model.Service;
import com.dietiestates2025.dieti.model.VisitBooking;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.ImageRepository;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;
import com.dietiestates2025.dieti.repositories.OfferRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.RoleRepository;
import com.dietiestates2025.dieti.repositories.SaleRepository;
import com.dietiestates2025.dieti.repositories.ServiceRepository;
import com.dietiestates2025.dieti.repositories.VisitBookingRepository;

import java.util.List;


@SpringBootApplication
public class DietiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietiApplication.class, args);
    }

    /*@Bean
	public CommandLineRunner commandLineRunner(RegionRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<Object[]> results = repo.findAllRegionWithProvinces();
			for (Object[] result : results) {
				String provinceName = (String) result[0];
				String regionName = (String) result[1];
				System.out.println("Province: " + provinceName + ", Region: " + regionName);
			}


		};
	}*/
	//tested Municipality, Agency, User, Province, Region
	@Bean
	public CommandLineRunner commandLineRunner(VisitBookingRepository repo) {
		return args -> {
			// Usa la query personalizzata per caricare le regioni con le province
			List<VisitBooking> results = repo.findAll();
			// Print each result in the list
			results.forEach(element -> System.out.println(element));
		};
}
}

