package com.wareflow.warehouse.mapper;

import com.wareflow.warehouse.dto.WarehouseResponse;
import com.wareflow.warehouse.entity.Warehouse;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getDescription(),
                warehouse.isActive(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt()
        );
    }
}