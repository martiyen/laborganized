package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContainerServiceTests {

    @Mock
    private ContainerRepository containerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContainerService containerService;

    private User user;
    private Container container;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        container = new Container();
        container.setId(1L);
        container.setName("Test Container");
        container.setTemperature(4.0);
        container.setUser(user);
        container.setStoreableList(new ArrayList<>());
    }

    @Test
    void findAll_ShouldReturnContainerList() {
        when(containerRepository.findAll()).thenReturn(Collections.singletonList(container));
        List<ContainerDTO> result = containerService.findAll();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Container", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(containerRepository.findById(1L)).thenReturn(Optional.of(container));
        ContainerDTO result = containerService.findById(1L);
        assertNotNull(result);
        assertEquals("Test Container", result.getName());
    }

    @Test
    void findUserContainer_ShouldReturnContainersOfUser() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(containerRepository.findAllByUser(user)).thenReturn(Collections.singletonList(container));

        List<ContainerDTO> result = containerService.findUserContainers();
        assertFalse(result.isEmpty());
        assertEquals("Test Container", result.get(0).getName());
    }

    @Test
    void save_ShouldSaveContainer() {
        ContainerCreateRequest request = new ContainerCreateRequest("New Container", 4.0, 100, 1L, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(containerRepository.save(any(Container.class))).thenReturn(container);

        ContainerDTO result = containerService.save(request);
        assertNotNull(result);
        assertEquals("Test Container", result.getName());
    }

    @Test
    void delete_ShouldDeleteContainer() {
        when(containerRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> containerService.delete(1L));
        verify(containerRepository, times(1)).deleteById(1L);
    }

    @Test
    void update_ShouldModifyContainer() {
        ContainerDTO containerDTO = new ContainerDTO(container);
        containerDTO.setName("updated name");
        when(containerRepository.findById(1L)).thenReturn(Optional.of(container));
        when(containerRepository.save(any(Container.class))).thenReturn(container);

        ContainerDTO result = containerService.update(containerDTO);
        assertNotNull(result);
        assertEquals("updated name", result.getName());
    }
}
