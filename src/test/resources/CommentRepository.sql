create sequence comment_seq;
create schema comment;
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