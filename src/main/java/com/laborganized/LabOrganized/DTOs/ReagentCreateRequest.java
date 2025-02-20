package com.laborganized.LabOrganized.DTOs;

import java.time.LocalDate;

public record ReagentCreateRequest(
        String name,
        Long userId,
        Long containerId,
        String supplier,
        String reference,
        Double quantity,
        String unit,
        String concentration,
        LocalDate expirationDate,
        String comments
){
}
