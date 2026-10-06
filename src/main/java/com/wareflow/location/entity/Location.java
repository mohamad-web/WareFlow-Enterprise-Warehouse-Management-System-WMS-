package com.wareflow.location.entity;

import com.wareflow.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(
        name = "locations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_locations_warehouse_code",
                        columnNames = {
                                "warehouse_id",
                                "code"
                        }
                )
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "warehouse_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_locations_warehouse"
            )
    )
    private Warehouse warehouse;

    @Column(
            name = "code",
            nullable = false,
            length = 100
    )
    private String code;

    @Column(
            name = "name",
            length = 150
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 30
    )
    private LocationType type;

    @Column(name = "zone", length = 50)
    private String zone;

    @Column(name = "aisle", length = 50)
    private String aisle;

    @Column(name = "rack", length = 50)
    private String rack;

    @Column(name = "bin", length = 50)
    private String bin;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @CreatedDate
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(
            name = "updated_at",
            nullable = false
    )
    private OffsetDateTime updatedAt;

    protected Location() {
    }

    public Location(
            Warehouse warehouse,
            String code,
            String name,
            LocationType type,
            String zone,
            String aisle,
            String rack,
            String bin
    ) {
        this.warehouse = Objects.requireNonNull(warehouse);
        this.code = Objects.requireNonNull(code);
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.zone = zone;
        this.aisle = aisle;
        this.rack = rack;
        this.bin = bin;
        this.active = true;
    }

    public void updateDetails(
            String name,
            LocationType type,
            String zone,
            String aisle,
            String rack,
            String bin
    ) {
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.zone = zone;
        this.aisle = aisle;
        this.rack = rack;
        this.bin = bin;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public LocationType getType() {
        return type;
    }

    public String getZone() {
        return zone;
    }

    public String getAisle() {
        return aisle;
    }

    public String getRack() {
        return rack;
    }

    public String getBin() {
        return bin;
    }

    public boolean isActive() {
        return active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Location other)) {
            return false;
        }

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}