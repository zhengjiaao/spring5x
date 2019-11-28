-- 创建表
-- 用户表 ：如果表不存在，则创建，id自增且是主键，username不能null
CREATE TABLE IF NOT EXISTS `userentity`(
   `id` INT UNSIGNED AUTO_INCREMENT,
   `username` VARCHAR(100) NOT NULL,
   `age` INT,
   PRIMARY KEY ( `id` )
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

COMMIT;
