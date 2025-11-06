package com.dietiestates2025.dieti;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.PropertyState;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.PropertyStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository repo;

    @Mock
    private PropertyStateRepository propertyStateRepository;
    
    @InjectMocks
    private PropertyService propertyService;

    @Test
    void updatePropertyState_WhenStateIsValid_ShouldCallRepositoryUpdate() {
        // GIVEN: un ID proprietà e un nome di stato valido
        int propertyId = 10;
        String newStateName = "Sold";
        PropertyState soldState = new PropertyState();
        soldState.setId(2); // ID fittizio per lo stato "Sold"
        soldState.setState(newStateName);

        // Configura il mock per trovare lo stato
        when(propertyStateRepository.findByStateIgnoreCase(newStateName)).thenReturn(Optional.of(soldState));

        // WHEN: si chiama il metodo per aggiornare lo stato
        propertyService.updatePropertyState(propertyId, newStateName);

        // THEN: il metodo `updatePropertyState` del repository viene chiamato con i dati corretti
        verify(propertyStateRepository).findByStateIgnoreCase(newStateName);
        verify(repo).updatePropertyState(propertyId, soldState.getId());
        verifyNoMoreInteractions(repo, propertyStateRepository);
    }

    @Test
    void updatePropertyState_WhenStateIsInvalid_ShouldThrowResourceNotFoundException() {
        // GIVEN: un ID proprietà e un nome di stato non valido
        int propertyId = 20;
        String invalidStateName = "NonExistentState";

        // Configura il mock per NON trovare lo stato
        when(propertyStateRepository.findByStateIgnoreCase(invalidStateName)).thenReturn(Optional.empty());

        // WHEN/THEN: ci si aspetta che venga lanciata un'eccezione
        assertThrows(ResourceNotFoundException.class, 
            () -> propertyService.updatePropertyState(propertyId, invalidStateName));

        // Verifica che il metodo di update del repository NON sia mai stato chiamato
        verify(repo, never()).updatePropertyState(anyInt(), anyInt());
    }
}