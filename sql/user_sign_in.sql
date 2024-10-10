-- 用户签到表
CREATE TABLE user_sign_in (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- 主键，自动递增
      userId BIGINT NOT NULL,               -- 用户ID，关联用户表
      signDate DATE NOT NULL,            -- 签到日期
      createdTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 记录创建时间
      UNIQUE KEY uq_user_date (userId, signDate)  -- 用户ID和签到日期的唯一性约束
);


INSERT INTO user_sign_in (userId, signDate) VALUES (1, '2024-10-01');
INSERT INTO user_sign_in (userId, signDate) VALUES (1, '2024-10-03');
INSERT INTO user_sign_in (userId, signDate) VALUES (1, '2024-10-04');
INSERT INTO user_sign_in (userId, signDate) VALUES (1, '2024-09-03');