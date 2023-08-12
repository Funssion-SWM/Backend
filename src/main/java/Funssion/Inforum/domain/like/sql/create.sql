CREATE TABLE member.like (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    post_type varchar NOT NULL,
    post_id int8 NOT NULL,
    created timestamp DEFAULT current_timestamp
);