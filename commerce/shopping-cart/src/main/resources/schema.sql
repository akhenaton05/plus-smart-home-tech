CREATE TABLE cart (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    cart_state VARCHAR(20) NOT NULL
);

CREATE TABLE cart_products (
    id UUID PRIMARY KEY,
    cart_id UUID,
    product_id UUID,
    quantity BIGINT,
    FOREIGN KEY (cart_id) REFERENCES cart (id)
);