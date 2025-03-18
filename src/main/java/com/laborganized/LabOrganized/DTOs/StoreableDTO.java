package com.laborganized.LabOrganized.DTOs;

import com.laborganized.LabOrganized.models.Storeable;

public class StoreableDTO {
    private Long id;
    private String name;
    private Long userId;
    private Long containerId;

    public StoreableDTO() {
    }

    public StoreableDTO(Long id, String name, Long userId, Long containerId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.containerId = containerId;
    }

    public StoreableDTO(Storeable storeable) {
        this.id = storeable.getId();
        this.name = storeable.getName();
        this.userId = storeable.getUser().getId();
        this.containerId = storeable.getContainer() != null ? storeable.getContainer().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getContainerId() {
        return containerId;
    }

    public void setContainerId(Long containerId) {
        this.containerId = containerId;
    }
}
