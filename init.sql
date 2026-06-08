-- ============================================================
-- OPRPP Hotel Application - DDL Init Script
-- Run this in pgAdmin Query Tool on your target database
-- ============================================================

DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS hotel_amenity CASCADE;
DROP TABLE IF EXISTS hotel CASCADE;
DROP TABLE IF EXISTS amenity CASCADE;
DROP TABLE IF EXISTS city CASCADE;
DROP TABLE IF EXISTS app_user CASCADE;

-- ============================================================
-- TABLES
-- ============================================================

CREATE TABLE city (
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL
);

CREATE TABLE amenity (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE app_user (
    id       SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(20)  NOT NULL CHECK (role IN ('ADMINISTRATOR', 'KORISNIK'))
);

CREATE TABLE hotel (
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(200)   NOT NULL,
    address         VARCHAR(300)   NOT NULL,
    num_rooms       INTEGER        NOT NULL CHECK (num_rooms > 0),
    price_per_night NUMERIC(10, 2) NOT NULL CHECK (price_per_night > 0),
    description     TEXT,
    image_path      VARCHAR(500),
    category        INTEGER        NOT NULL CHECK (category BETWEEN 1 AND 5),
    city_id         INTEGER        NOT NULL REFERENCES city(id)
);

CREATE TABLE hotel_amenity (
    hotel_id   INTEGER NOT NULL REFERENCES hotel(id)   ON DELETE CASCADE,
    amenity_id INTEGER NOT NULL REFERENCES amenity(id) ON DELETE CASCADE,
    PRIMARY KEY (hotel_id, amenity_id)
);

CREATE TABLE review (
    id         SERIAL PRIMARY KEY,
    hotel_id   INTEGER   NOT NULL REFERENCES hotel(id) ON DELETE CASCADE,
    user_id    INTEGER   NOT NULL REFERENCES app_user(id),
    rating     INTEGER   NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- PROCEDURES
-- ============================================================

CREATE OR REPLACE PROCEDURE insert_city(p_name VARCHAR, p_country VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO city(name, country) VALUES (p_name, p_country);
END;
$$;

CREATE OR REPLACE PROCEDURE update_city(p_id INTEGER, p_name VARCHAR, p_country VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE city SET name = p_name, country = p_country WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_city(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM city WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE insert_amenity(p_name VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO amenity(name) VALUES (p_name)
    ON CONFLICT (name) DO NOTHING;
END;
$$;

CREATE OR REPLACE PROCEDURE update_amenity(p_id INTEGER, p_name VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE amenity SET name = p_name WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_amenity(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM amenity WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE insert_user(p_username VARCHAR, p_password VARCHAR, p_role VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO app_user(username, password, role) VALUES (p_username, p_password, p_role);
END;
$$;

CREATE OR REPLACE PROCEDURE update_user(p_id INTEGER, p_username VARCHAR, p_password VARCHAR, p_role VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE app_user SET username = p_username, password = p_password, role = p_role WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_user(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM app_user WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE insert_hotel(
    p_name VARCHAR, p_address VARCHAR, p_num_rooms INTEGER,
    p_price NUMERIC, p_description TEXT, p_image_path VARCHAR,
    p_category INTEGER, p_city_id INTEGER
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO hotel(name, address, num_rooms, price_per_night, description, image_path, category, city_id)
    VALUES (p_name, p_address, p_num_rooms, p_price, p_description, p_image_path, p_category, p_city_id);
END;
$$;

CREATE OR REPLACE PROCEDURE update_hotel(
    p_id INTEGER, p_name VARCHAR, p_address VARCHAR, p_num_rooms INTEGER,
    p_price NUMERIC, p_description TEXT, p_image_path VARCHAR,
    p_category INTEGER, p_city_id INTEGER
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE hotel SET
        name            = p_name,
        address         = p_address,
        num_rooms       = p_num_rooms,
        price_per_night = p_price,
        description     = p_description,
        image_path      = p_image_path,
        category        = p_category,
        city_id         = p_city_id
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_hotel(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM hotel WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE link_hotel_amenity(p_hotel_id INTEGER, p_amenity_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO hotel_amenity(hotel_id, amenity_id)
    VALUES (p_hotel_id, p_amenity_id)
    ON CONFLICT DO NOTHING;
END;
$$;

CREATE OR REPLACE PROCEDURE unlink_hotel_amenity(p_hotel_id INTEGER, p_amenity_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM hotel_amenity WHERE hotel_id = p_hotel_id AND amenity_id = p_amenity_id;
END;
$$;

CREATE OR REPLACE PROCEDURE insert_review(
    p_hotel_id INTEGER, p_user_id INTEGER, p_rating INTEGER, p_comment TEXT
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO review(hotel_id, user_id, rating, comment)
    VALUES (p_hotel_id, p_user_id, p_rating, p_comment);
END;
$$;

CREATE OR REPLACE PROCEDURE update_review(p_id INTEGER, p_rating INTEGER, p_comment TEXT)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE review SET rating = p_rating, comment = p_comment WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_review(p_id INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM review WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE delete_all_data()
LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM review;
    DELETE FROM hotel_amenity;
    DELETE FROM hotel;
    DELETE FROM amenity;
    DELETE FROM city;
END;
$$;

-- ============================================================
-- FUNCTIONS (SELECT) - all hotel functions JOIN city
-- ============================================================

CREATE OR REPLACE FUNCTION get_all_cities()
RETURNS TABLE(id INTEGER, name VARCHAR, country VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT c.id, c.name, c.country FROM city c ORDER BY c.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_city_by_id(p_id INTEGER)
RETURNS TABLE(id INTEGER, name VARCHAR, country VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT c.id, c.name, c.country FROM city c WHERE c.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_amenities()
RETURNS TABLE(id INTEGER, name VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT a.id, a.name FROM amenity a ORDER BY a.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_amenity_by_id(p_id INTEGER)
RETURNS TABLE(id INTEGER, name VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT a.id, a.name FROM amenity a WHERE a.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_amenities_by_hotel(p_hotel_id INTEGER)
RETURNS TABLE(id INTEGER, name VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT a.id, a.name
        FROM amenity a
        JOIN hotel_amenity ha ON ha.amenity_id = a.id
        WHERE ha.hotel_id = p_hotel_id
        ORDER BY a.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_users()
RETURNS TABLE(id INTEGER, username VARCHAR, password VARCHAR, role VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT u.id, u.username, u.password, u.role FROM app_user u ORDER BY u.username;
END;
$$;

CREATE OR REPLACE FUNCTION get_user_by_id(p_id INTEGER)
RETURNS TABLE(id INTEGER, username VARCHAR, password VARCHAR, role VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY SELECT u.id, u.username, u.password, u.role FROM app_user u WHERE u.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_user_by_username(p_username VARCHAR)
RETURNS TABLE(id INTEGER, username VARCHAR, password VARCHAR, role VARCHAR)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT u.id, u.username, u.password, u.role
        FROM app_user u WHERE u.username = p_username;
END;
$$;

CREATE OR REPLACE FUNCTION get_all_hotels()
RETURNS TABLE(
    id INTEGER, name VARCHAR, address VARCHAR, num_rooms INTEGER,
    price_per_night NUMERIC, description TEXT, image_path VARCHAR,
    category INTEGER, city_id INTEGER, city_name VARCHAR, city_country VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT h.id, h.name, h.address, h.num_rooms,
               h.price_per_night, h.description, h.image_path,
               h.category, h.city_id, c.name, c.country
        FROM hotel h
        JOIN city c ON c.id = h.city_id
        ORDER BY h.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_hotel_by_id(p_id INTEGER)
RETURNS TABLE(
    id INTEGER, name VARCHAR, address VARCHAR, num_rooms INTEGER,
    price_per_night NUMERIC, description TEXT, image_path VARCHAR,
    category INTEGER, city_id INTEGER, city_name VARCHAR, city_country VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT h.id, h.name, h.address, h.num_rooms,
               h.price_per_night, h.description, h.image_path,
               h.category, h.city_id, c.name, c.country
        FROM hotel h
        JOIN city c ON c.id = h.city_id
        WHERE h.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION get_hotels_by_city(p_city_id INTEGER)
RETURNS TABLE(
    id INTEGER, name VARCHAR, address VARCHAR, num_rooms INTEGER,
    price_per_night NUMERIC, description TEXT, image_path VARCHAR,
    category INTEGER, city_id INTEGER, city_name VARCHAR, city_country VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT h.id, h.name, h.address, h.num_rooms,
               h.price_per_night, h.description, h.image_path,
               h.category, h.city_id, c.name, c.country
        FROM hotel h
        JOIN city c ON c.id = h.city_id
        WHERE h.city_id = p_city_id
        ORDER BY h.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_hotels_by_category(p_category INTEGER)
RETURNS TABLE(
    id INTEGER, name VARCHAR, address VARCHAR, num_rooms INTEGER,
    price_per_night NUMERIC, description TEXT, image_path VARCHAR,
    category INTEGER, city_id INTEGER, city_name VARCHAR, city_country VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT h.id, h.name, h.address, h.num_rooms,
               h.price_per_night, h.description, h.image_path,
               h.category, h.city_id, c.name, c.country
        FROM hotel h
        JOIN city c ON c.id = h.city_id
        WHERE h.category = p_category
        ORDER BY h.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_hotels_by_amenity(p_amenity_id INTEGER)
RETURNS TABLE(
    id INTEGER, name VARCHAR, address VARCHAR, num_rooms INTEGER,
    price_per_night NUMERIC, description TEXT, image_path VARCHAR,
    category INTEGER, city_id INTEGER, city_name VARCHAR, city_country VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT h.id, h.name, h.address, h.num_rooms,
               h.price_per_night, h.description, h.image_path,
               h.category, h.city_id, c.name, c.country
        FROM hotel h
        JOIN city c ON c.id = h.city_id
        JOIN hotel_amenity ha ON ha.hotel_id = h.id
        WHERE ha.amenity_id = p_amenity_id
        ORDER BY h.name;
END;
$$;

CREATE OR REPLACE FUNCTION get_reviews_by_hotel(p_hotel_id INTEGER)
RETURNS TABLE(
    id INTEGER, hotel_id INTEGER, user_id INTEGER,
    rating INTEGER, comment TEXT, created_at TIMESTAMP, username VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT r.id, r.hotel_id, r.user_id,
               r.rating, r.comment, r.created_at, u.username
        FROM review r
        JOIN app_user u ON u.id = r.user_id
        WHERE r.hotel_id = p_hotel_id
        ORDER BY r.created_at DESC;
END;
$$;

CREATE OR REPLACE FUNCTION get_review_by_id(p_id INTEGER)
RETURNS TABLE(
    id INTEGER, hotel_id INTEGER, user_id INTEGER,
    rating INTEGER, comment TEXT, created_at TIMESTAMP, username VARCHAR
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
        SELECT r.id, r.hotel_id, r.user_id,
               r.rating, r.comment, r.created_at, u.username
        FROM review r
        JOIN app_user u ON u.id = r.user_id
        WHERE r.id = p_id;
END;
$$;

CREATE OR REPLACE FUNCTION insert_hotel_returning_id(
    p_name VARCHAR, p_address VARCHAR, p_num_rooms INTEGER,
    p_price NUMERIC, p_description TEXT, p_image_path VARCHAR,
    p_category INTEGER, p_city_id INTEGER
)
RETURNS INTEGER
LANGUAGE plpgsql AS $$
DECLARE
    new_id INTEGER;
BEGIN
    INSERT INTO hotel(name, address, num_rooms, price_per_night, description, image_path, category, city_id)
    VALUES (p_name, p_address, p_num_rooms, p_price, p_description, p_image_path, p_category, p_city_id)
    RETURNING id INTO new_id;
    RETURN new_id;
END;
$$;

-- ============================================================
-- SEED DATA
-- ============================================================

CALL insert_user('admin', 'admin123', 'ADMINISTRATOR');

-- ============================================================
-- END
-- ============================================================
