create sequence comment_seq;

create table comment.info(
    id int8 primary key DEFAULT nextval('comment_seq'::regclass),
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

insert into comment.info (author_id, author_image_path, author_name, post_type, post_id, comment_text, created_date)
values (1,"image_path","writer_name","MEMO",1,"test for comment_text",2023-09-01);
