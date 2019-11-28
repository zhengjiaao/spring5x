-- 增量更新数据

-- 增加用户表数据
insert into userentity(username,age)
values ('王五',16);
insert into userentity(username,age)
values ('李四',18);
insert into userentity(username,age)
values ('阿里',19);

COMMIT;