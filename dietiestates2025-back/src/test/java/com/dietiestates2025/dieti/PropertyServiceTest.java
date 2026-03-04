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
        int propertyId = 10;
        String newStateName = "Sold";
        PropertyState soldState = new PropertyState();
        soldState.setId(2);
        soldState.setState(newStateName);

        when(propertyStateRepository.findByStateIgnoreCase(newStateName)).thenReturn(Optional.of(soldState));

        propertyService.updatePropertyState(propertyId, newStateName);

        verify(propertyStateRepository).findByStateIgnoreCase(newStateName);
        verify(repo).updatePropertyState(propertyId, soldState.getId());
        verifyNoMoreInteractions(repo, propertyStateRepository);
    }

    @Test
    void updatePropertyState_WhenStateIsInvalid_ShouldThrowResourceNotFoundException() {
        int propertyId = 20;
        String invalidStateName = "NonExistentState";

        when(propertyStateRepository.findByStateIgnoreCase(invalidStateName)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> propertyService.updatePropertyState(propertyId, invalidStateName));

        verify(repo, never()).updatePropertyState(anyInt(), anyInt());
    }
}