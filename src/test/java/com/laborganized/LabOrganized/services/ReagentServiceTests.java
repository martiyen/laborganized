package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.exceptions.ReagentNotFoundException;
import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.Reagent;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReagentServiceTests {

    @Mock
    private ReagentRepository reagentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContainerRepository containerRepository;

    @InjectMocks
    private ReagentService reagentService;

    private User testUser;
    private Container testContainer;
    private Reagent testReagent;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testContainer = new Container();
        testContainer.setId(1L);
        testContainer.setName("Container 1");

        testReagent = new Reagent();
        testReagent.setId(1L);
        testReagent.setName("Reagent 1");
        testReagent.setUser(testUser);
        testReagent.setContainer(testContainer);
    }

    @Test
    void findAll_ShouldFindAllReagents() {
        when(reagentRepository.findAll()).thenReturn(Collections.singletonList(testReagent));

        List<ReagentDTO> result = reagentService.findAll();

        assertEquals(1, result.size());
        assertEquals("Reagent 1", result.get(0).getName());
    }

    @Test
    void findById_ShouldReturnReagent_WhenExists() {
        when(reagentRepository.findById(1L)).thenReturn(Optional.of(testReagent));

        ReagentDTO result = reagentService.findById(1L);

        assertEquals("Reagent 1", result.getName());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(reagentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReagentNotFoundException.class, () -> reagentService.findById(99L));
    }

    @Test
    void findUserReagents_ShouldReturnUserReagents() {
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(auth.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(reagentRepository.findAllByUser(testUser)).thenReturn(Collections.singletonList(testReagent));

        List<ReagentDTO> result = reagentService.findUserReagents();

        assertEquals(1, result.size());
        assertEquals("Reagent 1", result.get(0).getName());
    }

    @Test
    void save_ShouldSaveReagent() {
        ReagentCreateRequest request = new ReagentCreateRequest("Reagent 2", 1L, 1L, "Supplier", "Ref", 10.0, "g", "10mM", LocalDate.now(), "Some comments");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(containerRepository.findById(1L)).thenReturn(Optional.of(testContainer));
        when(reagentRepository.save(any(Reagent.class))).thenReturn(testReagent);

        ReagentDTO result = reagentService.save(request);

        assertEquals("Reagent 1", result.getName());
    }

    @Test
    void delete_ShouldDeleteReagent_WhenExists() {
        when(reagentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reagentRepository).deleteById(1L);

        assertDoesNotThrow(() -> reagentService.delete(1L));
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(reagentRepository.existsById(99L)).thenReturn(false);

        assertThrows(ReagentNotFoundException.class, () -> reagentService.delete(99L));
    }

    @Test
    void update_ShouldUpdateReagent_WhenExists() {
        ReagentDTO updatedDTO = new ReagentDTO(testReagent);
        updatedDTO.setName("Updated Reagent");
        when(reagentRepository.findById(1L)).thenReturn(Optional.of(testReagent));
        when(reagentRepository.save(any(Reagent.class))).thenReturn(testReagent);

        ReagentDTO result = reagentService.update(updatedDTO);

        assertEquals("Updated Reagent", result.getName());
    }
}
