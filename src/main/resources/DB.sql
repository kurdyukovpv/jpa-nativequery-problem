create table tst_a
(
    id   BIGSERIAL primary key,
    name text
);

create table tst_b
(
    id    BIGSERIAL primary key,
    name  text,
    value bigint
);

insert into tst_b (name, value)
values ('First B', 0);

