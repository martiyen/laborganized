package com.laborganized.LabOrganized.controllers;

import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> allUsers = userService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    @PostMapping
    public ResponseEntity<UserDTO> addNewUser(@RequestBody UserCreateRequest request) {
        UserDTO savedUser = userService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
