package com.wareflow.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateWarehouseRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description
) {
}