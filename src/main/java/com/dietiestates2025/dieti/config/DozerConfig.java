package com.dietiestates2025.dieti.config;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe di configurazione Spring dedicata all'inizializzazione e alla personalizzazione
 * della libreria di mapping Dozer.
 * Centralizza la logica di mappatura tra le entità del dominio (es. {@link com.dietiestates2025.dieti.model.Property})
 * e i loro Data Transfer Objects (DTO) (es. {@link com.dietiestates2025.dieti.dto.PropertyDTO}).
 * Questo approccio promuove il riutilizzo del codice e la coerenza nelle conversioni di oggetti
 * in tutta l'applicazione.
 */
@Configuration
public class DozerConfig {
  /**
   * Crea e configura l'istanza principale di {@link DozerBeanMapper}, rendendola disponibile
   * per l'iniezione di dipendenze (Dependency Injection) in altri componenti, come i service.
   * La configurazione include una mappatura personalizzata definita tramite un {@link BeanMappingBuilder}
   * per gestire correttamente le relazioni tra oggetti complessi e annidati.
   *
   * @return Un'istanza di {@link DozerBeanMapper} configurata con le mappature personalizzate del progetto.
   */
  @Bean(name = "org.dozer.Mapper")
  public DozerBeanMapper dozerBean() {
    DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();

    // Aggiunge un blocco di mappature personalizzate al mapper.
    dozerBeanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        // Definiamo qui le regole di mappatura esplicite tra le entità del modello e i DTO.

        // Mappatura tra l'entità Property e il suo DTO.
        // `.fields("address", "address")` indica a Dozer di mappare ricorsivamente anche l'oggetto Address annidato.
        // `.fields("services", "services")` fa lo stesso per la collezione di Service.
        mapping(com.dietiestates2025.dieti.model.Property.class, com.dietiestates2025.dieti.dto.PropertyDTO.class)
          .fields("address", "address")
          .fields("services", "services");
        // --- Mappature a cascata per gli oggetti annidati nell'indirizzo ---

        // Mappatura tra l'entità Address e il suo DTO, specificando di mappare l'oggetto Municipality.
        mapping(com.dietiestates2025.dieti.model.Address.class, com.dietiestates2025.dieti.dto.AddressDTO.class)
          .fields("municipality", "municipality");
        // Mappatura tra l'entità Municipality e il suo DTO, specificando di mappare l'oggetto Province.
        mapping(com.dietiestates2025.dieti.model.Municipality.class, com.dietiestates2025.dieti.dto.MunicipalityDTO.class)
          .fields("province", "province");

        mapping(com.dietiestates2025.dieti.model.Province.class, com.dietiestates2025.dieti.dto.ProvinceDTO.class)
          .fields("region", "region");

        mapping(com.dietiestates2025.dieti.model.Region.class, com.dietiestates2025.dieti.dto.RegionDTO.class);

        mapping(com.dietiestates2025.dieti.model.Service.class, com.dietiestates2025.dieti.dto.ServiceDTO.class);
      }
    });

    return dozerBeanMapper;
  }
}

