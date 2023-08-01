create table member.USER(
    id int8 primary key DEFAULT nextval('user_id_seq'::regclass),
    name VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(60) not null unique,
    login_type INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP
);

create table Member.auth(
    id int8 primary key default nextval('auth_id_seq'::regclass),
    user_id INT NOT NULL,
    password VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES member.user(id)
);
-- db migration sql sequence<nextval>(auto_increment) 설정으로 인한 pk insert 생략--
insert into member.user(id,name,email,login_type,created_date)
    select B.user_id B.user_name, A.user_email, B.login_type,B.created_date
    from member.auth A join member.member_user B on A.user_id = B.user_id

insert into member.auth(id,user_id,password)
    select A.auth_id, B.id, A.user_pw
    from member.setting A join member.user B on A.user_id = B.id