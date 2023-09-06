CREATE SCHEMA member;
CREATE SCHEMA memo;

CREATE TABLE "memo"."info" (
    memo_id bigserial PRIMARY KEY,
    author_id int8 NOT NULL,
    author_name varchar,
    author_image_path varchar,
    memo_title varchar(255) NOT NULL,
    memo_description varchar(255),
    memo_text jsonb,
    memo_color varchar(50),
    likes int8 NOT NULL DEFAULT 0,
    is_temporary boolean NOT NULL DEFAULT false,
    created_date timestamp,
    updated_date timestamp
);

CREATE TABLE "member"."auth" (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    password varchar(100) NOT NULL
);

CREATE TABLE "member"."auth_code" (
    id bigserial,
    email varchar(60) NOT NULL,
    code bpchar(6) NOT NULL,
    expiration bool NULL DEFAULT false,
    due_date timestamp NULL
);

CREATE TABLE "member"."history" (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    memo_cnt int8 NOT NULL DEFAULT 0,
    blog_cnt int8 NOT NULL DEFAULT 0,
    question_cnt int8 NOT NULL DEFAULT 0,
    date date NOT NULL DEFAULT current_date
);

CREATE TABLE "member"."like"  (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    post_type varchar NOT NULL,
    post_id int8 NOT NULL,
    created timestamp DEFAULT current_timestamp
);

CREATE TABLE "member"."user" (
    id bigserial PRIMARY KEY,
    name varchar(15) NOT NULL,
    email varchar(60) NOT NULL,
    login_type int8 NOT NULL DEFAULT 0,
    introduce varchar(100),
    tags varchar(60),
    image_path varchar(300),
    created_date timestamp
);

create schema comment;
create schema member;
create table comment.info(
    id serial primary key,
    author_id int8 not null,
    author_image_path varchar(300),
    author_name VARCHAR(15) not null,
    post_type varchar not null,
    post_id int8 not null,
    likes int8 not null default 0,
    re_comments int8 not null default 0,
    comment_text text not null,
    created_date timestamp,
    updated_date timestamp
);

CREATE TABLE member.like_comment (
    id serial primary key,
    user_id int8 NOT NULL,
    comment_id int8 NOT NULL,
    is_recomment bool NOT NULL,
    CONSTRAINT like_comment_user_id_comment_id_is_recomment_key UNIQUE (user_id, comment_id, is_recomment)
);

CREATE TABLE comment.re_comments (
    id serial primary key,
    author_id int8 NOT NULL,
    author_image_path varchar(300) NULL,
    author_name varchar(15) NOT NULL,
    likes int8 NOT NULL DEFAULT 0,
    parent_id int8 NOT NULL,
    comment_text text NOT NULL,
    created_date timestamp NULL,
    updated_date timestamp NULL
);