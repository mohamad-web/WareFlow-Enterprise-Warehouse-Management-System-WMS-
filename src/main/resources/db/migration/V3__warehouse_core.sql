CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,

                            code VARCHAR(50) NOT NULL,
                            name VARCHAR(150) NOT NULL,
                            description VARCHAR(500),

                            active BOOLEAN NOT NULL DEFAULT TRUE,

                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT uk_warehouses_code
                                UNIQUE (code)
);


CREATE TABLE locations (
                           id BIGSERIAL PRIMARY KEY,

                           warehouse_id BIGINT NOT NULL,

                           code VARCHAR(100) NOT NULL,
                           name VARCHAR(150),

                           type VARCHAR(30) NOT NULL,

                           zone VARCHAR(50),
                           aisle VARCHAR(50),
                           rack VARCHAR(50),
                           bin VARCHAR(50),

                           active BOOLEAN NOT NULL DEFAULT TRUE,

                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_locations_warehouse
                               FOREIGN KEY (warehouse_id)
                                   REFERENCES warehouses (id)
                                   ON DELETE RESTRICT,

                           CONSTRAINT uk_locations_warehouse_code
                               UNIQUE (warehouse_id, code),

                           CONSTRAINT ck_locations_type
                               CHECK (
                                   type IN (
                                            'RECEIVING',
                                            'STORAGE',
                                            'PICKING',
                                            'PACKING',
                                            'SHIPPING',
                                            'QUARANTINE'
                                       )
                                   )
);


CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,

                          sku VARCHAR(100) NOT NULL,
                          name VARCHAR(200) NOT NULL,
                          description VARCHAR(1000),

                          unit_of_measure VARCHAR(30) NOT NULL,

                          active BOOLEAN NOT NULL DEFAULT TRUE,

                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT uk_products_sku
                              UNIQUE (sku),

                          CONSTRAINT ck_products_unit_of_measure
                              CHECK (
                                  unit_of_measure IN (
                                                      'PIECE',
                                                      'BOX',
                                                      'PALLET',
                                                      'KILOGRAM',
                                                      'LITER'
                                      )
                                  )
);


CREATE TABLE inventory (
                           id BIGSERIAL PRIMARY KEY,

                           product_id BIGINT NOT NULL,
                           location_id BIGINT NOT NULL,

                           quantity NUMERIC(19, 3) NOT NULL DEFAULT 0,

                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_inventory_product
                               FOREIGN KEY (product_id)
                                   REFERENCES products (id)
                                   ON DELETE RESTRICT,

                           CONSTRAINT fk_inventory_location
                               FOREIGN KEY (location_id)
                                   REFERENCES locations (id)
                                   ON DELETE RESTRICT,

                           CONSTRAINT uk_inventory_product_location
                               UNIQUE (product_id, location_id),

                           CONSTRAINT ck_inventory_quantity_non_negative
                               CHECK (quantity >= 0)
);


CREATE TABLE stock_movements (
                                 id BIGSERIAL PRIMARY KEY,

                                 product_id BIGINT NOT NULL,

                                 movement_type VARCHAR(30) NOT NULL,
                                 adjustment_direction VARCHAR(20),

                                 from_location_id BIGINT,
                                 to_location_id BIGINT,

                                 quantity NUMERIC(19, 3) NOT NULL,

                                 reference VARCHAR(100),
                                 note VARCHAR(1000),

                                 performed_by_user_id BIGINT NOT NULL,

                                 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_stock_movements_product
                                     FOREIGN KEY (product_id)
                                         REFERENCES products (id)
                                         ON DELETE RESTRICT,

                                 CONSTRAINT fk_stock_movements_from_location
                                     FOREIGN KEY (from_location_id)
                                         REFERENCES locations (id)
                                         ON DELETE RESTRICT,

                                 CONSTRAINT fk_stock_movements_to_location
                                     FOREIGN KEY (to_location_id)
                                         REFERENCES locations (id)
                                         ON DELETE RESTRICT,

                                 CONSTRAINT fk_stock_movements_performed_by_user
                                     FOREIGN KEY (performed_by_user_id)
                                         REFERENCES app_users (id)
                                         ON DELETE RESTRICT,

                                 CONSTRAINT ck_stock_movements_quantity_positive
                                     CHECK (quantity > 0),

                                 CONSTRAINT ck_stock_movements_type
                                     CHECK (
                                         movement_type IN (
                                                           'RECEIPT',
                                                           'PUTAWAY',
                                                           'TRANSFER',
                                                           'PICK',
                                                           'ADJUSTMENT',
                                                           'SHIPMENT'
                                             )
                                         ),

                                 CONSTRAINT ck_stock_movements_adjustment_direction
                                     CHECK (
                                         adjustment_direction IS NULL
                                             OR adjustment_direction IN (
                                                                         'INCREASE',
                                                                         'DECREASE'
                                             )
                                         ),

                                 CONSTRAINT ck_stock_movements_adjustment_rules
                                     CHECK (
                                         (
                                             movement_type = 'ADJUSTMENT'
                                                 AND adjustment_direction IS NOT NULL
                                             )
                                             OR
                                         (
                                             movement_type <> 'ADJUSTMENT'
                                                 AND adjustment_direction IS NULL
                                             )
                                         ),

                                 CONSTRAINT ck_stock_movements_locations_different
                                     CHECK (
                                         from_location_id IS NULL
                                             OR to_location_id IS NULL
                                             OR from_location_id <> to_location_id
                                         )
);


CREATE INDEX idx_locations_warehouse_id
    ON locations (warehouse_id);

CREATE INDEX idx_locations_type
    ON locations (type);


CREATE INDEX idx_inventory_product_id
    ON inventory (product_id);

CREATE INDEX idx_inventory_location_id
    ON inventory (location_id);


CREATE INDEX idx_stock_movements_product_id
    ON stock_movements (product_id);

CREATE INDEX idx_stock_movements_from_location_id
    ON stock_movements (from_location_id);

CREATE INDEX idx_stock_movements_to_location_id
    ON stock_movements (to_location_id);

CREATE INDEX idx_stock_movements_performed_by_user_id
    ON stock_movements (performed_by_user_id);

CREATE INDEX idx_stock_movements_created_at
    ON stock_movements (created_at);

CREATE INDEX idx_stock_movements_type
    ON stock_movements (movement_type);