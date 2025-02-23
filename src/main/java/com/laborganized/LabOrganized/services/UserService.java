package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.UserCreateRequest;
import com.laborganized.LabOrganized.DTOs.UserDTO;
import com.laborganized.LabOrganized.exceptions.UserNotFoundException;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.models.UserRole;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> findAll() {
        List<UserDTO> userDTOs = new ArrayList<>();

        userRepository.findAll().forEach(user -> userDTOs.add(new UserDTO(user)));

        return userDTOs;
    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        return new UserDTO(user);
    }

    @Transactional
    public UserDTO save(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setPasswordHash(request.passwordHash());
        user.setEmail(request.email());
        user.setCreated(LocalDateTime.now());
        user.setLastUpdated(LocalDateTime.now());
        user.setUserRole(UserRole.ADMIN);
        user.setStoreableList(new ArrayList<>());

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser);
    }

    @Transactional
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Transactional
    public UserDTO update(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setLastUpdated(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return new UserDTO(savedUser);
    }
}
