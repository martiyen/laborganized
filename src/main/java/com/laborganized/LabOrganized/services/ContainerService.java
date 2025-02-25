package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.ContainerCreateRequest;
import com.laborganized.LabOrganized.DTOs.ContainerDTO;
import com.laborganized.LabOrganized.exceptions.ContainerNotFoundException;
import com.laborganized.LabOrganized.exceptions.UserNotFoundException;
import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
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
    @Autowired
    private ReagentRepository reagentRepository;

    public List<ContainerDTO> findAll() {
        List<ContainerDTO> containerDTOS = new ArrayList<>();

        containerRepository.findAll().forEach(
                container -> containerDTOS.add(new ContainerDTO(container))
        );

        return containerDTOS;
    }

    public ContainerDTO findById(Long id) {
        Container container = containerRepository.findById(id).orElseThrow(
                () -> new ContainerNotFoundException("Container not found")
        );

        return new ContainerDTO(container);
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
            container.setTemperature(parentContainer.getTemperature());
        }

        Container savedContainer = containerRepository.save(container);

        return new ContainerDTO(savedContainer);
    }

    @Transactional
    public void delete(Long id) {
        if (containerRepository.existsById(id)) {
            containerRepository.deleteById(id);
        } else {
            throw new ContainerNotFoundException("Container not found");
        }
    }

    @Transactional
    public ContainerDTO update(ContainerDTO containerDTO) {
        Container container = containerRepository.findById(containerDTO.getId())
                .orElseThrow(
                        () -> new ContainerNotFoundException("Container not found")
                );

        container.setName(containerDTO.getName());
        container.setTemperature(containerDTO.getTemperature());
        container.setCapacity(containerDTO.getCapacity());

        Container updatedContainer = containerRepository.save(container);

        return new ContainerDTO(updatedContainer);
    }
}
