package com.wareflow.location.repository;

import com.wareflow.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository
        extends JpaRepository<Location, Long> {

    boolean existsByWarehouseIdAndCode(
            Long warehouseId,
            String code
    );

    Optional<Location> findByWarehouseIdAndCode(
            Long warehouseId,
            String code
    );
}