package com.laborganized.LabOrganized.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.services.ContainerService;
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

@WebMvcTest(controllers = { ContainerController.class })
@AutoConfigureMockMvc(addFilters = false)
public class ContainerControllerTests {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private ContainerService containerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAll_ShouldReturnAllContainers() throws Exception {
        ContainerDTO containerDTO = new ContainerDTO(1L, "Container 1", 1L, null, -80.0, 81, new ArrayList<>());
        when(containerService.findAll()).thenReturn(List.of(containerDTO));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/containers/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_ShouldReturnContainer_WhenValidId() throws Exception {
        ContainerDTO containerDTO = new ContainerDTO(1L, "Container 1", 1L, null, -80.0, 81, new ArrayList<>());
        when(containerService.findById(1L)).thenReturn(containerDTO);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/containers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(containerDTO.getName()));
    }

    @Test
    void getUserContainers_ShouldReturnUserContainers() throws Exception {
        ContainerDTO containerDTO = new ContainerDTO(1L, "Container 1", 1L, null, -80.0, 81, new ArrayList<>());
        when(containerService.findUserContainers()).thenReturn(List.of(containerDTO));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/containers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_ShouldReturnNewContainer() throws Exception {
        ContainerCreateRequest containerCreateRequest = new ContainerCreateRequest("Container 1", -80.0, 81, 1L, null);
        ContainerDTO savedContainer = new ContainerDTO(1L, "Container 1", 1L, null, -80.0, 81, new ArrayList<>());
        when(containerService.save(any(ContainerCreateRequest.class))).thenReturn(savedContainer);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/containers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(containerCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Container 1"));
    }

    @Test
    void deleteContainer_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(containerService).delete(1L);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/containers/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Container deleted successfully"));
    }

    @Test
    void updateContainer_ShouldReturnUpdatedContainer() throws Exception {
        ContainerDTO updatedContainer = new ContainerDTO(1L, "Container 1", 1L, null, -80.0, 81, new ArrayList<>());
        when(containerService.update(any(ContainerDTO.class))).thenReturn(updatedContainer);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/containers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedContainer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedContainer.getName()));
    }
}
