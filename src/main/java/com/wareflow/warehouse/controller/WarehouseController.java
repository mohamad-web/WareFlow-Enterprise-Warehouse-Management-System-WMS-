package com.wareflow.warehouse.controller;

import com.wareflow.warehouse.dto.CreateWarehouseRequest;
import com.wareflow.warehouse.dto.UpdateWarehouseRequest;
import com.wareflow.warehouse.dto.WarehouseResponse;
import com.wareflow.warehouse.service.WarehouseCommandService;
import com.wareflow.warehouse.service.WarehouseQueryService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseCommandService warehouseCommandService;
    private final WarehouseQueryService warehouseQueryService;

    public WarehouseController(
            WarehouseCommandService warehouseCommandService,
            WarehouseQueryService warehouseQueryService
    ) {
        this.warehouseCommandService = warehouseCommandService;
        this.warehouseQueryService = warehouseQueryService;
    }

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(
            @Valid @RequestBody CreateWarehouseRequest request
    ) {
        WarehouseResponse response =
                warehouseCommandService.createWarehouse(request);

        URI location =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.id())
                        .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(
            @PathVariable Long warehouseId
    ) {
        return ResponseEntity.ok(
                warehouseQueryService.getWarehouseById(
                        warehouseId
                )
        );
    }

    @GetMapping
    public ResponseEntity<Page<WarehouseResponse>> getAllWarehouses(
            @PageableDefault(
                    size = 20,
                    sort = "code"
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                warehouseQueryService.getAllWarehouses(pageable)
        );
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable Long warehouseId,
            @Valid @RequestBody UpdateWarehouseRequest request
    ) {
        return ResponseEntity.ok(
                warehouseCommandService.updateWarehouse(
                        warehouseId,
                        request
                )
        );
    }

    @PatchMapping("/{warehouseId}/activate")
    public ResponseEntity<WarehouseResponse> activateWarehouse(
            @PathVariable Long warehouseId
    ) {
        return ResponseEntity.ok(
                warehouseCommandService.activateWarehouse(
                        warehouseId
                )
        );
    }

    @PatchMapping("/{warehouseId}/deactivate")
    public ResponseEntity<WarehouseResponse> deactivateWarehouse(
            @PathVariable Long warehouseId
    ) {
        return ResponseEntity.ok(
                warehouseCommandService.deactivateWarehouse(
                        warehouseId
                )
        );
    }
}