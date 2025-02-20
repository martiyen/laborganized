package com.laborganized.LabOrganized.controllers;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.services.ReagentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/reagents")
public class ReagentController {
    @Autowired
    private ReagentService reagentService;

    @GetMapping
    public ResponseEntity<List<ReagentDTO>> getAll() {
        List<ReagentDTO> reagentList = reagentService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(reagentList);
    }

    @PostMapping
    public ResponseEntity<ReagentDTO> create(@RequestBody ReagentCreateRequest request) {
        ReagentDTO savedReagent = reagentService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedReagent);
    }
}
