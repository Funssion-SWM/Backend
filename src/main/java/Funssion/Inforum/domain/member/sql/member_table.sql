create table Member.user(
    user_id INT AUTO_INCREMENT,
    user_name VARCHAR(15) NOT NULL UNIQUE,
    login_type INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP,
    PRIMARY KEY (user_id)
);

create table Member.auth(
    auth_id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    user_email VARCHAR(60) NOT NULL UNIQUE,
    user_pw VARCHAR(100) NOT NULL,
    PRIMARY KEY (auth_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);