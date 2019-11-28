-- 创建表
-- MYSQL的数据库引擎中，只有InnoDB和BDB（Berkley DB ）包括了对事务处理和外键的支持。如果数据引擎建为MyISAM则事务回退rollback无效

-- 测试表 userentity ：如果表不存在，则创建，id自增且是主键，username不能null
CREATE TABLE IF NOT EXISTS userentity(
   id INT,
   username VARCHAR(255),
   age INT,
   createtime DATE,
   updatetime TIMESTAMP(6),
   PRIMARY KEY ( id )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 菜单表-自关联
CREATE TABLE IF NOT EXISTS t_menu (
       menuId integer not null,
        menuName varchar(255),
        parentId integer,
        primary key (menuId)
    ) engine=InnoDB;
ALTER TABLE t_menu
       add constraint FKpm8dvkadv6q1gv5svcydjrp7c
       foreign key (parentId)
       references t_menu (menuId);

COMMIT;