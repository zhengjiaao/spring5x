--插入数据语句

-- userentity用户表数据准备
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小明',21) $$
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小刘',22) $$
insert into USERENTITY(id,username,age) values (SEQ_MY_USER.NEXTVAL,'小王',20) $$

-- student学生表数据准备
insert into STUDENT(stuid,stuname,stuage,email) values (SEQ_MY_STUDENT.NEXTVAL,'学生1',20,'abc@qq.com') $$
insert into STUDENT(stuid,stuname,stuage,email) values (SEQ_MY_STUDENT.NEXTVAL,'学生2',21,'abc@qq.com') $$
insert into STUDENT(stuid,stuname,stuage,email) values (SEQ_MY_STUDENT.NEXTVAL,'学生3',22,'abc@qq.com') $$

COMMIT $$