package com.laborganized.LabOrganized.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { UserController.class })
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAll_ShouldFindAllUsers() throws Exception {
        UserDTO user = new UserDTO(1L, "user1", "User One", "user1@example.com", new ArrayList<>());
        when(userService.findAll()).thenReturn(List.of(user));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_ShouldReturnUser_WhenValidId() throws Exception {
        UserDTO user = new UserDTO(1L, "user1", "User One", "user1@example.com", new ArrayList<>());
        when(userService.findById(1L)).thenReturn(user);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void addNewUser_ShouldReturnCreatedUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest("user1", "User One", "password", "user1@example.com");
        UserDTO savedUser = new UserDTO(1L, "user1", "User One", "user1@example.com", new ArrayList<>());
        when(userService.save(any(UserCreateRequest.class))).thenReturn(savedUser);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedUser.getId()));
    }

    @Test
    void deleteUserById_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void updateUserById_ShouldReturnUpdatedUser() throws Exception {
        UserDTO updatedUser = new UserDTO(1L, "userUpdated", "Updated User", "updated@example.com", new ArrayList<>());
        when(userService.update(any(UserDTO.class))).thenReturn(updatedUser);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUser.getUsername()));
    }
}