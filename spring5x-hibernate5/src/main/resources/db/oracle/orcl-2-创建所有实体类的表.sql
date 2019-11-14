--oracle建表语句

--表作用说明：
create table userentity
(
	id NUMBER(19) not null
		primary key,
	username VARCHAR2(255 char),
	createtime TIMESTAMP(6),
	age NUMBER(19)
);
