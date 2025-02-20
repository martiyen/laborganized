package com.laborganized.LabOrganized.DTOs;

public record ContainerCreateRequest(
        String name,
        Double temperature,
        Integer capacity,
        Long userId,
        Long containerId
) {}
