-- 增量更新数据

-- 增加USERENTITY表数据
insert into USERENTITY(id,username,age)
values (SEQ_MY_HIBERNATE.NEXTVAL,'明月',16);

insert into USERENTITY(id,username,age)
values (SEQ_MY_HIBERNATE.NEXTVAL,'秦时',18);

insert into USERENTITY(id,username,age)
values (SEQ_MY_HIBERNATE.NEXTVAL,'阿里',19);
COMMIT;