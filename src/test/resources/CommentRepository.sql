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