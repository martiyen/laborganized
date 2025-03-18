package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.exceptions.UniqueConstraintViolationException;
import com.laborganized.LabOrganized.exceptions.UserNotFoundException;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setCreated(LocalDateTime.now());
        user.setLastUpdated(LocalDateTime.now());
        user.setRoles("ROLE_MANAGER,ROLE_USER");
        user.setStoreableList(new ArrayList<>());
    }

    @Test
    void findAll_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserDTO> result = userService.findAll();

        Assertions.assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void findById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(2L));
    }

    @Test
    void save_ShouldCreateNewUser() {
        UserCreateRequest request = new UserCreateRequest("newuser", "New User", "password", "new@example.com");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserDTO result = userService.save(request);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void save_ShouldThrowException_WhenUsernameExists() {
        UserCreateRequest request = new UserCreateRequest("testuser", "New User", "new@example.com", "password");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UniqueConstraintViolationException.class, () -> userService.save(request));
    }

    @Test
    void update_ShouldModifyExistingUser() {
        UserDTO userDTO = new UserDTO(1L, "updatedUser", "Updated Name", "updated@example.com", new ArrayList<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("updatedUser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        UserDTO result = userService.update(userDTO);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteById(1L));
    }

    @Test
    void deleteById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteById(2L));
    }
}
