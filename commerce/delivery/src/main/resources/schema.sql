CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    country VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(100),
    house VARCHAR(50),
    flat VARCHAR(50)
);

CREATE TABLE deliveries (
    delivery_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    from_address_id UUID,
    to_address_id UUID,
    state VARCHAR(50) NOT NULL,
    FOREIGN KEY (from_address_id) REFERENCES addresses(id),
    FOREIGN KEY (to_address_id) REFERENCES addresses(id)
);