package com.laborganized.LabOrganized.controllers;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.services.ReagentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/reagents")
public class ReagentController {
    @Autowired
    private ReagentService reagentService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<ReagentDTO>> getAll() {
        List<ReagentDTO> reagentList = reagentService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(reagentList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReagentDTO> getById(@PathVariable Long id) {
        ReagentDTO reagent = reagentService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(reagent);
    }

    @GetMapping
    public ResponseEntity<List<ReagentDTO>> getUserReagents() {
        List<ReagentDTO> userReagents = reagentService.findUserReagents();

        return ResponseEntity.status(HttpStatus.OK).body(userReagents);
    }

    @PostMapping
    public ResponseEntity<ReagentDTO> create(@RequestBody ReagentCreateRequest request) {
        ReagentDTO savedReagent = reagentService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReagent);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        reagentService.delete(id);

        return "Reagent deleted successfully";
    }

    @PutMapping
    public ResponseEntity<ReagentDTO> update(@RequestBody ReagentDTO reagentDTO) {
        ReagentDTO updatedReagent = reagentService.update(reagentDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedReagent);
    }
}
