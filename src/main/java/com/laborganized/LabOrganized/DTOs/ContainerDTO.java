package com.laborganized.LabOrganized.DTOs;

import com.laborganized.LabOrganized.models.Container;
import com.laborganized.LabOrganized.models.Storeable;

import java.util.List;
import java.util.stream.Collectors;

public class ContainerDTO extends StoreableDTO {
    private Double temperature;
    private Integer capacity;
    private List<StoreableDTO> storeableList;

    public ContainerDTO() {
    }

    public ContainerDTO(Long id, String name, Long userId, Long containerId, Double temperature, Integer capacity, List<StoreableDTO> storeableList) {
        super(id, name, userId, containerId);
        this.temperature = temperature;
        this.capacity = capacity;
        this.storeableList = storeableList;
    }

    public ContainerDTO(Container container) {
        super(container);
        this.temperature = container.getTemperature();
        this.capacity = container.getCapacity();
        this.storeableList = container.getStoreableList().stream()
                .map(StoreableDTO::new)
                .collect(Collectors.toList());
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public List<StoreableDTO> getStoreableList() {
        return storeableList;
    }

    public void setStoreableList(List<StoreableDTO> storeableList) {
        this.storeableList = storeableList;
    }
}
