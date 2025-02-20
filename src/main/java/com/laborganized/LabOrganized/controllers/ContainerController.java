package com.laborganized.LabOrganized.controllers;

import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.services.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/containers")
public class ContainerController {
    @Autowired
    private ContainerService containerService;

    @GetMapping
    public ResponseEntity<List<ContainerDTO>> getAll() {
        List<ContainerDTO> allContainers = containerService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allContainers);
    }

    @PostMapping
    public ResponseEntity<ContainerDTO> create(@RequestBody ContainerCreateRequest request) {
        ContainerDTO savedContainer = containerService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedContainer);
    }
}
