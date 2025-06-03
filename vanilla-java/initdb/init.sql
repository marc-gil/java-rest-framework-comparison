DROP TABLE IF EXISTS constellations;

CREATE TABLE constellations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hemisphere VARCHAR(20) CHECK (hemisphere IN ('Northern', 'Southern', 'Equatorial')),
    description VARCHAR(100) NOT NULL
);

INSERT INTO constellations (name, hemisphere, description) VALUES ('Orion', 'Equatorial', 'Orion has 81 starts');
