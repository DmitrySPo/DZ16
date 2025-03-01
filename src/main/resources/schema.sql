CREATE TABLE IF NOT EXISTS recipes (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS ingredients (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    quantity DOUBLE NOT NULL,
    recipe_id BIGINT NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id)
);