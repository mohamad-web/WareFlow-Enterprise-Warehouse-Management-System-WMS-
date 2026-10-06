package com.wareflow.warehouse.service;

import com.wareflow.warehouse.dto.WarehouseResponse;
import com.wareflow.warehouse.entity.Warehouse;
import com.wareflow.warehouse.exception.WarehouseNotFoundException;
import com.wareflow.warehouse.mapper.WarehouseMapper;
import com.wareflow.warehouse.repository.WarehouseRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WarehouseQueryService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseQueryService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    public WarehouseResponse getWarehouseById(
            Long warehouseId
    ) {
        Warehouse warehouse =
                warehouseRepository.findById(warehouseId)
                        .orElseThrow(
                                () -> new WarehouseNotFoundException(
                                        warehouseId
                                )
                        );

        return warehouseMapper.toResponse(warehouse);
    }

    public Page<WarehouseResponse> getAllWarehouses(
            Pageable pageable
    ) {
        return warehouseRepository
                .findAll(pageable)
                .map(warehouseMapper::toResponse);
    }
}