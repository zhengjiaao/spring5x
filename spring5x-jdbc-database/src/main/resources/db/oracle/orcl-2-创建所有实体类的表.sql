--oracle建表语句

--表作用说明：
CREATE TABLE userentity(
   id NUMBER not null,
   username VARCHAR(255) not null,
   age int,
   PRIMARY KEY ( id )
);

COMMIT;