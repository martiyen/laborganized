package com.laborganized.LabOrganized.services;

import com.laborganized.LabOrganized.DTOs.ReagentCreateRequest;
import com.laborganized.LabOrganized.DTOs.ReagentDTO;
import com.laborganized.LabOrganized.exceptions.ContainerNotFoundException;
import com.laborganized.LabOrganized.exceptions.ReagentNotFoundException;
import com.laborganized.LabOrganized.exceptions.UserNotFoundException;
import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.Reagent;
import com.laborganized.LabOrganized.models.User;
import com.laborganized.LabOrganized.repositories.ContainerRepository;
import com.laborganized.LabOrganized.repositories.ReagentRepository;
import com.laborganized.LabOrganized.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReagentService {
    @Autowired
    private ReagentRepository reagentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContainerRepository containerRepository;

    public List<ReagentDTO> findAll() {
        List<ReagentDTO> reagentList = new ArrayList<>();

        reagentRepository.findAll().forEach(
                reagent -> reagentList.add(new ReagentDTO(reagent))
        );

        return reagentList;
    }

    public ReagentDTO findById(Long id) {
        Reagent reagent = reagentRepository.findById(id).orElseThrow(
                () -> new ReagentNotFoundException("Reagent not found")
        );

        return new ReagentDTO(reagent);
    }

    public List<ReagentDTO> findUserReagents() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User not found")
        );
        List<ReagentDTO> reagentList = new ArrayList<>();

        reagentRepository.findAllByUser(user).forEach(
                reagent -> reagentList.add(new ReagentDTO(reagent))
        );

        return reagentList;
    }

    @Transactional
    public ReagentDTO save(ReagentCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found.")
                );

        Container container = containerRepository.findById(request.containerId())
                .orElseThrow(
                        () -> new ContainerNotFoundException("Container not found.")
                );

        Reagent reagent = new Reagent();
        reagent.setName(request.name());
        reagent.setUser(user);
        reagent.setContainer(container);
        reagent.setSupplier(request.supplier());
        reagent.setReference(request.reference());
        reagent.setQuantity(request.quantity());
        reagent.setUnit(request.unit());
        reagent.setConcentration(request.concentration());
        reagent.setExpirationDate(request.expirationDate());
        reagent.setComments(request.comments());

        Reagent savedReagent = reagentRepository.save(reagent);

        return new ReagentDTO(savedReagent);
    }

    @Transactional
    public void delete(Long id) {
        if (reagentRepository.existsById(id)) {
            reagentRepository.deleteById(id);
        } else {
            throw new ReagentNotFoundException("Reagent not found");
        }
    }

    @Transactional
    public ReagentDTO update(ReagentDTO reagentDTO) {
        Reagent reagent = reagentRepository.findById(reagentDTO.getId())
                .orElseThrow(
                        () -> new ReagentNotFoundException("Reagent not found")
                );

        reagent.setName(reagentDTO.getName());
        reagent.setSupplier(reagentDTO.getSupplier());
        reagent.setReference(reagentDTO.getReference());
        reagent.setQuantity(reagentDTO.getQuantity());
        reagent.setUnit(reagentDTO.getUnit());
        reagent.setConcentration(reagentDTO.getConcentration());
        reagent.setExpirationDate(reagentDTO.getExpirationDate());
        reagent.setComments(reagentDTO.getComments());

        Reagent savedReagent = reagentRepository.save(reagent);

        return new ReagentDTO(savedReagent);
    }


}
