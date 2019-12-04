-- 删除测试的表和数据
declare
    countCol number;
    countTab number;
    countSeq number;
begin
--===============20191203==================start
    -- 删除无用表  upper：小写字符转化成大写的函数
    select count(*) into countTab from user_tables where table_name = upper('userentity');
    if countTab = 1 then
        execute immediate 'drop table userentity';
    end if;
    -- 删除无用序列 名称区分大小写
    select count(*) into countSeq from user_sequences where sequence_name = 'SEQ_MY_HIBERNATE';
    if countSeq = 1 then
        execute immediate 'DROP SEQUENCE SEQ_MY_HIBERNATE';
    end if;
--===============20191203==================end
end;$$