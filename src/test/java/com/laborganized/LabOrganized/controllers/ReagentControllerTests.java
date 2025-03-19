package com.laborganized.LabOrganized.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.services.ReagentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { ReagentController.class })
@AutoConfigureMockMvc(addFilters = false)
public class ReagentControllerTests {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private ReagentService reagentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAll_ShouldReturnAllContainers() throws Exception {
        ReagentDTO reagentDTO = new ReagentDTO(1L, "Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", LocalDate.now().plusYears(3), "Comments");
        when(reagentService.findAll()).thenReturn(List.of(reagentDTO));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/reagents/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_ShouldReturnReagent_WhenValidId() throws Exception {
        ReagentDTO reagentDTO = new ReagentDTO(1L, "Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", LocalDate.now().plusYears(3), "Comments");
        when(reagentService.findById(1L)).thenReturn(reagentDTO);

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/reagents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(reagentDTO.getName()));
    }

    @Test
    void getUserReagents_ShouldReturnUserReagents() throws Exception {
        ReagentDTO reagentDTO = new ReagentDTO(1L, "Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", LocalDate.now().plusYears(3), "Comments");
        when(reagentService.findUserReagents()).thenReturn(List.of(reagentDTO));

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/reagents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_ShouldReturnNewReagent() throws Exception {
        ReagentCreateRequest reagentCreateRequest = new ReagentCreateRequest("Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", null, "Comments");
        ReagentDTO savedReagent = new ReagentDTO(1L, "Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", LocalDate.now().plusYears(3), "Comments");
        when(reagentService.save(any(ReagentCreateRequest.class))).thenReturn(savedReagent);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/reagents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reagentCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Reagent 1"));
    }

    @Test
    void deleteReagent_ShouldReturnSuccessMessage() throws Exception {
        doNothing().when(reagentService).delete(1L);

        mvc.perform(MockMvcRequestBuilders.delete("/api/v1/reagents/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reagent deleted successfully"));
    }

    @Test
    void updateReagent_ShouldReturnUpdatedReagent() throws Exception {
        ReagentDTO updatedReagent = new ReagentDTO(1L, "Reagent 1", 1L, 1L, "Supplier", "Reference", 10.0, "mL", "Concentration", null, "Comments");
        when(reagentService.update(any(ReagentDTO.class))).thenReturn(updatedReagent);

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/reagents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReagent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedReagent.getName()));
    }
}
