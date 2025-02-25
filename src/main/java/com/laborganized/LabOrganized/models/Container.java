package com.laborganized.LabOrganized.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Entity
@Table(name = "containers")
public class Container extends Storeable {
    private Double temperature;
    @Positive(message = "Capacity must be a positive number")
    private Integer capacity;
    @OneToMany(mappedBy = Storeable_.CONTAINER, cascade = CascadeType.REMOVE)
    private List<Storeable> storeableList;

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

    public List<Storeable> getStoreableList() {
        return storeableList;
    }

    public void setStoreableList(List<Storeable> storeableList) {
        this.storeableList = storeableList;
    }
}
