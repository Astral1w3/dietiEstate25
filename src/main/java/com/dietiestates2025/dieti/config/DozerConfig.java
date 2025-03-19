package com.dietiestates2025.dieti.config;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DozerConfig {

  @Bean(name = "org.dozer.Mapper")
  public DozerBeanMapper dozerBean() {
    DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();


    // Aggiungi una configurazione programmatica per evitare errori di ricorsione
    dozerBeanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        // Configurazione personalizzata delle mappature per evitare ricorsione infinita
        mapping(com.dietiestates2025.dieti.model.Property.class, com.dietiestates2025.dieti.dto.PropertyDTO.class)
          .fields("address", "address")
          .fields("services", "services");

        mapping(com.dietiestates2025.dieti.model.Address.class, com.dietiestates2025.dieti.dto.AddressDTO.class)
          .fields("municipality", "municipality");

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

