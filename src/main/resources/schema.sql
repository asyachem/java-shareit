
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT,
    request_id BIGINT
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR
);

CREATE TABLE IF NOT EXISTS requests (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
   description VARCHAR,
   requestor_id BIGINT,
   created TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
   id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
   text VARCHAR,
   item_id BIGINT,
   author_id BIGINT,
   created TIMESTAMP,
   CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

ALTER TABLE items ADD FOREIGN KEY (owner_id) REFERENCES users (id);

ALTER TABLE items ADD FOREIGN KEY (request_id) REFERENCES requests (id);

ALTER TABLE bookings ADD FOREIGN KEY (item_id) REFERENCES items (id);

ALTER TABLE bookings ADD FOREIGN KEY (booker_id) REFERENCES users (id);

ALTER TABLE requests ADD FOREIGN KEY (requestor_id) REFERENCES users (id);

ALTER TABLE comments ADD FOREIGN KEY (author_id) REFERENCES users (id);
