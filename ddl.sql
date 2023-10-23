CREATE SCHEMA member;
create schema post;
create schema tag;
create schema score;
create sequence post.memo_series_order_seq start 1;

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
CREATE TABLE member.dislike  (
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
    follower_cnt int8 not null default 0,
    is_deleted bool not null default false,
    rank varchar(15) not null default 'BRONZE_5',
    score int8 not null default 0,
    daily_get_score int not null default 0,
    constraint limit_daily_get_score check (daily_get_score <= 200)
);

CREATE TABLE "member".follow (
    id bigserial NOT NULL,
    user_id int8 NOT NULL,
    followed_user_id int8 NOT NULL,
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member.like_comment (
    id serial primary key,
    user_id int8 NOT NULL,
    comment_id int8 NOT NULL,
    is_recomment bool NOT NULL,
    CONSTRAINT like_comment_user_id_comment_id_is_recomment_key UNIQUE (user_id, comment_id, is_recomment)
);

CREATE TABLE "member".notification (
    id bigserial PRIMARY KEY,
    receiver_id int8 NOT NULL,
    post_type_to_show varchar(10),
    post_id_to_show int8,
    sender_id int8 NOT NULL,
    sender_name varchar(15) NOT NULL,
    sender_image_path varchar(300),
    sender_post_type varchar(10),
    sender_post_id int8,
    sender_rank varchar(15) not null,
    notification_type varchar(20) NOT NULL,
    is_checked boolean not null default false,
    created timestamp NOT NULL DEFAULT current_timestamp
);

create table post.comment(
    id serial primary key,
    author_id int8 not null,
    author_image_path varchar(300),
    author_name VARCHAR(15) not null,
    author_rank varchar(15) not null,
    post_type varchar not null,
    post_id int8 not null,
    likes int8 not null default 0,
    recomments int8 not null default 0,
    comment_text text not null,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    is_user_delete boolean not null default false,
    CONSTRAINT non_negative_recomments CHECK (recomments >= 0)
);

CREATE TABLE post.recomment (
    id serial primary key,
    author_id int8 NOT NULL,
    author_image_path varchar(300) NULL,
    author_name varchar(15) NOT NULL,
    author_rank varchar(15) not null,
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


CREATE TABLE post.memo (
    id bigserial PRIMARY KEY,
    author_id int8 NOT NULL,
    author_name varchar,
    author_image_path varchar,
    author_rank varchar(15) not null,
    title varchar(255) NOT NULL,
    description varchar(255),
    text jsonb,
    color varchar(50),
    likes int8 NOT NULL DEFAULT 0,
    is_temporary boolean NOT NULL DEFAULT false,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    tags varchar array DEFAULT '{}',
    replies_count int8 not null default 0,
    is_created boolean NOT NULL DEFAULT true,
    question_count int8 not null default 0,
    series_id int8 null,
    series_title varchar(255) null,
    series_order int8 null,
    constraint non_negative_question_count check (question_count >= 0)
);

create table post.question(
    id bigserial primary key,
    author_id int8 NOT NULL,
    author_name varchar,
    author_image_path varchar,
    author_rank varchar(15) not null,
    title varchar(255) NOT NULL,
    description text null,
    text jsonb,
    likes int8 NOT NULL DEFAULT 0,
    is_solved boolean NOT NULL DEFAULT false,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    tags varchar array DEFAULT '{}',
    replies_count int8 not null default 0,
    answers int8 not null default 0,
    memo_id int8 not null,
    CONSTRAINT non_negative_replies_count CHECK (replies_count >= 0),
    constraint non_negative_answers_count check (answers >= 0)
);

create table post.answer(
    id bigserial primary key,
    question_id int8 not null,
    author_id int8 not null,
    author_name varchar,
    author_image_path varchar,
    author_rank varchar(15) not null,
    text jsonb,
    likes int8 NOT NULL DEFAULT 0,
    dislikes int8 NOT NULL DEFAULT 0,
    created_date timestamp default current_timestamp,
    updated_date timestamp default current_timestamp,
    is_selected boolean not null default false,
    replies_count int8 not null default 0,
    CONSTRAINT non_negative_replies_count CHECK (replies_count >= 0),
    CONSTRAINT non_negative_likes CHECK (likes >= 0),
    CONSTRAINT non_negative_dislikes CHECK (dislikes >= 0)
);

CREATE TABLE post.series (
    id bigserial NOT NULL,
    title varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    thumbnail_image_path varchar NULL,
    author_id int8 NOT NULL,
    author_name varchar(15) NOT NULL,
    author_image_path varchar NULL,
    author_rank varchar(15) not null,
    likes int4 NOT NULL DEFAULT 0,
    created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT non_negative_series_likes CHECK ((likes >= 0)),
    CONSTRAINT series_pkey PRIMARY KEY (id)
);


create table score.info (
    user_id int8 not null,
    score_type varchar(15) not null,
    -- score은 해당 작업으로 벌어들인 점수를 의미하며, 하루 최대 제한을 넘겼을 경우에 고정된 타입의 점수와 다를 경우를 추적하기 위함입니다.
    score int8 not null,
    post_id int8 not null,
    post_type varchar(10) not null default post,
    liked_author_id int8 null,
    created_date timestamp default current_timestamp,
    primary key (user_id,score_type,post_id)
);
