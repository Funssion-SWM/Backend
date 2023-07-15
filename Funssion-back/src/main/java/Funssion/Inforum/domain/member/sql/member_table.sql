CREATE TABLE  member.member_user (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(15) NOT NULL UNIQUE,
    login_type INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP
);

CREATE TABLE member.member_auth (
    auth_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    user_email VARCHAR(60) NOT NULL UNIQUE,
    user_pw VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES member_user (user_id)
);