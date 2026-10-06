package com.wareflow.warehouse.dto;

import java.time.OffsetDateTime;

public record WarehouseResponse(
        Long id,
        String code,
        String name,
        String description,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}