package com.laborganized.LabOrganized.controllers;

import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.services.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/containers")
public class ContainerController {
    @Autowired
    private ContainerService containerService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<List<ContainerDTO>> getAll() {
        List<ContainerDTO> allContainers = containerService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allContainers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContainerDTO> getById(@PathVariable Long id) {
        ContainerDTO container = containerService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(container);
    }

    @GetMapping()
    public ResponseEntity<List<ContainerDTO>> getUserContainers() {
        List<ContainerDTO> containers = containerService.findUserContainers();

        return ResponseEntity.status(HttpStatus.OK).body(containers);
    }

    @PostMapping
    public ResponseEntity<ContainerDTO> create(@RequestBody ContainerCreateRequest request) {
        ContainerDTO savedContainer = containerService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedContainer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        containerService.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body("Container deleted successfully");
    }

    @PutMapping
    public ResponseEntity<ContainerDTO> update(@RequestBody ContainerDTO containerDTO) {
        ContainerDTO updatedContainer = containerService.update(containerDTO);

        return ResponseEntity.status(HttpStatus.OK).body(updatedContainer);
    }

}
