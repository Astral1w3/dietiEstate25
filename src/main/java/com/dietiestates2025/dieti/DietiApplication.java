package com.dietiestates2025.dieti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dietiestates2025.dieti.model.Province;
import com.dietiestates2025.dieti.repositories.ProvinceRepository;

import java.util.List;

@SpringBootApplication
public class DietiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DietiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ProvinceRepository provinceRepository) {
        return args -> {
            // Esegui una query SELECT per recuperare tutte le province
            List<Province> provinces = provinceRepository.findAll();  // Cambiato a List<Province>
            
            // Stampa le province recuperate
            provinces.forEach(System.out::println);  // Uso del metodo di stampa direttamente
        };
    }

}

