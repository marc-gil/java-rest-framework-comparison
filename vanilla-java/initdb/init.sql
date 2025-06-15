DROP TABLE IF EXISTS constellations;

CREATE TABLE constellations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hemisphere VARCHAR(20) CHECK (hemisphere IN ('Northern', 'Southern', 'Equatorial')),
    description VARCHAR(100) NOT NULL
);

INSERT INTO constellations (name, hemisphere, description) VALUES ('Orion', 'Equatorial', 'Orion has 81 starts');

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password) VALUES ('test-user', 'test-password');
