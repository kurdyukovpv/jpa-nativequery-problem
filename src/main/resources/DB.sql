create table tst_a
(
    id   BIGSERIAL primary key,
    name text,
    b_id BIGINT

);

create table tst_b
(
    id    BIGSERIAL primary key,
    name  text,
    value bigint
);

alter table tst_a
    add CONSTRAINT tst_fk FOREIGN KEY (b_id) REFERENCES tst_b (ID) MATCH FULL ON UPDATE NO ACTION ON DELETE NO ACTION;

insert into tst_b (name, value)
values ('First B', 0);

