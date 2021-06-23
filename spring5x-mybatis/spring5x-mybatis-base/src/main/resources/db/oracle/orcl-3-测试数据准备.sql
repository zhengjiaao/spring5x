--插入数据语句

-- userentity用户表数据准备
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小明',21) $$
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小刘',22) $$
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小王',20) $$

COMMIT $$