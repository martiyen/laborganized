package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.exceptions.ContainerNotFoundException;
import com.laborganized.LabOrganized.exceptions.UserNotFoundException;
import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContainerService {
    @Autowired
    private ContainerRepository containerRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ContainerDTO> findAll() {
        List<ContainerDTO> containerDTOS = new ArrayList<>();

        containerRepository.findAll().forEach(
                container -> containerDTOS.add(new ContainerDTO(container))
        );

        return containerDTOS;
    }

    @Transactional
    public ContainerDTO save(ContainerCreateRequest request) {
        Container container = new Container();
        container.setName(request.name());
        container.setTemperature(request.temperature());
        container.setCapacity(request.capacity());
        container.setStoreableList(new ArrayList<>());

        User user = userRepository.findById(request.userId())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found.")
                );

        container.setUser(user);

        if (request.containerId() != null) {
            Container parentContainer = containerRepository.findById(request.containerId())
                    .orElseThrow(
                            () -> new ContainerNotFoundException("Container not found.")
                    );

            container.setContainer(parentContainer);
        }

        Container savedContainer = containerRepository.save(container);

        return new ContainerDTO(savedContainer);
    }
}
