--oracle建表语句

--表作用说明：
CREATE TABLE userentity(
   id int not null,
   username VARCHAR(255) not null,
   age int,
   createtime DATE,
   updatetime TIMESTAMP(6),
   PRIMARY KEY ( id )
);

COMMIT;