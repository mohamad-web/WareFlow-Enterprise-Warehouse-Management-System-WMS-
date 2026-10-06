package com.wareflow.warehouse.service;

import com.wareflow.warehouse.dto.CreateWarehouseRequest;
import com.wareflow.warehouse.dto.UpdateWarehouseRequest;
import com.wareflow.warehouse.dto.WarehouseResponse;
import com.wareflow.warehouse.entity.Warehouse;
import com.wareflow.warehouse.exception.WarehouseCodeAlreadyExistsException;
import com.wareflow.warehouse.exception.WarehouseNotFoundException;
import com.wareflow.warehouse.mapper.WarehouseMapper;
import com.wareflow.warehouse.repository.WarehouseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;

@Service
public class WarehouseCommandService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseCommandService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Transactional
    public WarehouseResponse createWarehouse(
            CreateWarehouseRequest request
    ) {
        Objects.requireNonNull(
                request,
                "Create warehouse request must not be null"
        );

        String normalizedCode =
                normalizeCode(request.code());

        if (warehouseRepository.existsByCode(normalizedCode)) {
            throw new WarehouseCodeAlreadyExistsException(
                    normalizedCode
            );
        }

        Warehouse warehouse = new Warehouse(
                normalizedCode,
                request.name().trim(),
                normalizeNullableText(request.description())
        );

        Warehouse savedWarehouse =
                warehouseRepository.save(warehouse);

        return warehouseMapper.toResponse(savedWarehouse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(
            Long warehouseId,
            UpdateWarehouseRequest request
    ) {
        Objects.requireNonNull(
                request,
                "Update warehouse request must not be null"
        );

        Warehouse warehouse =
                warehouseRepository.findById(warehouseId)
                        .orElseThrow(
                                () -> new WarehouseNotFoundException(
                                        warehouseId
                                )
                        );

        warehouse.updateDetails(
                request.name().trim(),
                normalizeNullableText(request.description())
        );

        warehouseRepository.flush();

        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse activateWarehouse(
            Long warehouseId
    ) {
        Warehouse warehouse =
                warehouseRepository.findById(warehouseId)
                        .orElseThrow(
                                () -> new WarehouseNotFoundException(
                                        warehouseId
                                )
                        );

        warehouse.activate();

        warehouseRepository.flush();

        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse deactivateWarehouse(
            Long warehouseId
    ) {
        Warehouse warehouse =
                warehouseRepository.findById(warehouseId)
                        .orElseThrow(
                                () -> new WarehouseNotFoundException(
                                        warehouseId
                                )
                        );

        warehouse.deactivate();

        warehouseRepository.flush();

        return warehouseMapper.toResponse(warehouse);
    }

    private String normalizeCode(String code) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }
}