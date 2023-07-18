create table Member.member_user(
    user_id SERIAL primary key,
    user_name VARCHAR(15) NOT NULL UNIQUE,
    login_type INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP
)

create table Member.member_auth(
    auth_id SERIAL,
    user_id INT NOT NULL,
    user_email VARCHAR(60) NOT NULL UNIQUE,
    user_pw VARCHAR(100) NOT NULL,
    PRIMARY KEY (auth_id),
    FOREIGN KEY (user_id) REFERENCES member.member_user(user_id)
);