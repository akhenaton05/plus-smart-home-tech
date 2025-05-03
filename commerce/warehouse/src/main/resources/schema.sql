CREATE TABLE warehouse_products (
    id UUID PRIMARY KEY,
    width DOUBLE PRECISION NOT NULL,
    height DOUBLE PRECISION NOT NULL,
    depth DOUBLE PRECISION NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    is_fragile BOOLEAN NOT NULL,
    quantity BIGINT NOT NULL
);

CREATE TABLE order_bookings (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID
);

CREATE TABLE order_booking_products (
    order_booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (order_booking_id, product_id),
    FOREIGN KEY (order_booking_id) REFERENCES order_bookings(id)
);