create sequence auth_code_seq;
create sequence profile_seq;
create sequence comment_seq;
create sequence re_comment_seq;
create sequence like_comment_seq;

create table member.info(
    id int8 primary key DEFAULT nextval('user_id_seq'::regclass),
    name VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(60) not null unique,
    image_path varchar(60),
    login_type INT NOT NULL DEFAULT 0,
    image_path varchar(300),
    tags varchar(60),
    introduce varchar(100),
    created_date TIMESTAMP
);

create table Member.auth(
    id int8 primary key default nextval('auth_id_seq'::regclass)
    user_id INT NOT NULL,
    password VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES member.info(id)
);

create table member.auth_code(
    id int8 primary key DEFAULT nextval('auth_code_seq'::regclass),
    email VARCHAR(60) not null,
    code CHAR(6) not null,
    expiration boolean default false,
    due_date timestamp
);

create table member.profile (
    id int8 primary key default nextval('profile_seq'::regclass),
    user_id int8 not null unique,

    introduce varchar(300),
    tags varchar(60),
)

create table post.comment(
    id int8 primary key DEFAULT nextval('comment_seq'::regclass),
    author_id int8 not null,
    author_image_path varchar(300),
    author_name VARCHAR(15) not null,
    post_type varchar not null,
    post_id int8 not null,
    likes int8 not null default 0,
    recomments int8 not null default 0,
    comment_text text not null,
    created_date timestamp,
    updated_date timestamp
);


create table post.recomment(
    id int8 primary key DEFAULT nextval('re_comment_seq'::regclass),
    author_id int8 not null,
    author_image_path varchar(300),
    author_name VARCHAR(15) not null,
    likes int8 not null default 0,
    parent_id int8 not null,
    comment_text text not null,
    created_date timestamp,
    updated_date timestamp
);

create table member.like_comment(
    id int8 primary key DEFAULT nextval('like_comment_seq'::regclass),
    user_id int8 not null,
    comment_id int8 not null,
    is_recomment boolean not null,
    unique(user_id,comment_id,is_recomment)
);


-- db migration sql sequence<nextval>(auto_increment) 설정으로 인한 pk insert 생략--
-- insert into member.info(id,name,email,login_type,created_date)
--     select B.user_id B.user_name, A.user_email, B.login_type,B.created_date
--     from member.auth A join member.member_user B on A.user_id = B.user_id
--
-- insert into member.auth(id,user_id,password)
--     select A.auth_id, B.id, A.user_pw
--     from member.setting A join member.info B on A.user_id = B.id