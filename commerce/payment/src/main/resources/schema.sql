CREATE TABLE payments (
    payment_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_cost DOUBLE PRECISION NOT NULL,
    delivery_cost DOUBLE PRECISION NOT NULL,
    tax DOUBLE PRECISION NOT NULL,
    total_cost DOUBLE PRECISION NOT NULL,
    state VARCHAR(50) NOT NULL
);