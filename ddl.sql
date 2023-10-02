CREATE SCHEMA member;
CREATE SCHEMA memo;
create schema comment;
create schema post;
create schema tag;
create schema question;

CREATE TABLE tag.memo_to_tag (
    memo_id bigserial,
    tag_id bigserial,
    primary key (memo_id, tag_id)
);

CREATE TABLE tag.info (
    id bigserial primary key,
    tag_name varchar,
    is_default boolean default (false),
    tag_count int8 default (1)
);
insert into tag.info(tag_name, is_default, tag_count)
values
    ('Backend', true, 0),
    ('Frontend', true, 0),
    ('AI', true, 0),
    ('DevOps', true, 0),
    ('Security', true, 0),
    ('DBA', true, 0),
    ('Java', true, 0),
    ('Python', true, 0),
    ('JavaScript', true, 0),
    ('TypeScript', true, 0),
    ('C', true, 0),
    ('C++', true, 0),
    ('Spring', true, 0),
    ('Nest', true, 0),
    ('Node', true, 0),
    ('Django', true, 0),
    ('AWS', true, 0),
    ('React', true, 0),
    ('Next', true, 0),
    ('Vue', true, 0),
    ('SQL', true, 0),
    ('NoSQL', true, 0),
    ('MySQL', true, 0),
    ('MongoDB', true, 0),
    ('PostgreSQL', true, 0);

CREATE TABLE memo.info (
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
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    tags varchar array DEFAULT '{}',
    replies_count int8 not null default 0
);

CREATE TABLE member.auth (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    password varchar(100) NOT NULL
);

CREATE TABLE member.auth_code (
    id bigserial,
    email varchar(60) NOT NULL,
    code char(6) NOT NULL,
    expiration bool NULL DEFAULT false,
    due_date timestamp NULL
);

CREATE TABLE member.history (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    memo_cnt int8 NOT NULL DEFAULT 0,
    blog_cnt int8 NOT NULL DEFAULT 0,
    question_cnt int8 NOT NULL DEFAULT 0,
    answer_cnt int8 NOT NULL DEFAULT 0,
    date date NOT NULL DEFAULT current_date
);

CREATE TABLE member.like  (
    id bigserial PRIMARY KEY,
    user_id int8 NOT NULL,
    post_type varchar NOT NULL,
    post_id int8 NOT NULL,
    created timestamp DEFAULT current_timestamp
);

CREATE TABLE member.info (
    id serial PRIMARY KEY,
    name varchar(15) NOT NULL,
    email varchar(60) NOT NULL,
    login_type int8 NOT NULL DEFAULT 0,
    introduce varchar(100),
    tags varchar array DEFAULT '{}',
    image_path varchar(300),
    created_date timestamp,
    follow_cnt int8 not null default 0,
    follower_cnt int8 not null default 0
);

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
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp
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
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp
);

CREATE TABLE post.search_history (
    id bigserial primary key,
    user_id int8 not null,
    search_text text,
    access_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_tag boolean
);

create table question.info(
    id bigserial primary key,
    author_id int8 NOT NULL,
    author_name varchar,
    author_image_path varchar,
    title varchar(255) NOT NULL,
    description text null,
    text jsonb,
    likes int8 NOT NULL DEFAULT 0,
    is_solved boolean NOT NULL DEFAULT false,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    tags varchar array DEFAULT '{}',
    answers int8 not null default 0,
    memo_id int8 not null
);

create table question.answer(
    id bigserial primary key,
    question_id int8 not null,
    author_id int8 not null,
    author_name varchar,
    author_image_path varchar,
    text jsonb,
    likes int8 NOT NULL DEFAULT 0,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    is_selected boolean not null default false,
    replies_count int8 not null default 0
);

CREATE TABLE "member".follow (
    id bigserial NOT NULL,
    user_id int8 NOT NULL,
    followed_user_id int8 NOT NULL,
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);