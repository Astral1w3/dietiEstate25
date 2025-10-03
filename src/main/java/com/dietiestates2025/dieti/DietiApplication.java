package com.dietiestates2025.dieti;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import com.dietiestates2025.dieti.Service.ImageService;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.repositories.AddressRepository;
import com.dietiestates2025.dieti.repositories.ImageRepository;
import com.dietiestates2025.dieti.repositories.MunicipalityRepository;


@SpringBootApplication
public class DietiApplication {

    private final ImageRepository imageRepository;

    private final ImageService imageService;

    DietiApplication(ImageService imageService, ImageRepository imageRepository) {
        this.imageService = imageService;
		this.imageRepository = imageRepository;
    }

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
				Image i = imageRepository.findById(1).get();
				byte[] prova = i.getImage();
		
				// Verifica la lunghezza dell'immagine
				int imageLength = prova.length;
				System.out.println("Lunghezza dell'immagine: " + imageLength);  // Stampa per il debug
		
				try (DataOutputStream dos = new DataOutputStream(new FileOutputStream("numbers.dat"))) {
					for (byte num : prova) {
						dos.writeByte(num);
					}
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