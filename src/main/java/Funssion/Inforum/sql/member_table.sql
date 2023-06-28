create table Member.user(
    user_id NUMBER AUTO_INCREMENT,
    user_name VARCHAR(15) NOT NULL UNIQUE,
    login_type INT NOT NULL DEFAULT 0,
    user_createdAt DATE,
    PRIMARY KEY (user_id)
);

create table Member.NonSocialUser(
    user_email_id NUMBER AUTO_INCREMENT,
    user_email VARCHAR(60) NOT NULL UNIQUE,
    user_pwd VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_email_id)
)