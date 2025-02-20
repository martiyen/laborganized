package com.laborganized.LabOrganized.DTOs;

public record UserCreateRequest(
        String username,
        String name,
        String passwordHash,
        String email
) {}