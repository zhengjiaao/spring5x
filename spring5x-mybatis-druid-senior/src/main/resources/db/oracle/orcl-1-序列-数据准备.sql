-- oracle创建序列语句

-- 创建用户序列
create sequence SEQ_MY_USER_HIBERNATE
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

-- 创建组序列
create sequence SEQ_MY_GROUP_HIBERNATE
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;

-- 创建订单序列
create sequence SEQ_MY_ORDERS_HIBERNATE
minvalue 1
maxvalue 9999999999999999999999999999
start with 1
increment by 1
cache 20;