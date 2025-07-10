-- Create Database
create database if not exists techInterviewWeb;

-- Switch Database
use  techInterviewWeb;

-- User Table
create table if not exists user
(
    id           bigint auto_increment comment 'ID' primary key,
    userAccount  varchar(256)                           not null comment 'Account',
    userPassword varchar(512)                           not null comment 'Password',
    unionId      varchar(256)                           null comment 'WeChat Open Platform ID',
    mpOpenId     varchar(256)                           null comment 'Public Account OpenID',
    userName     varchar(256)                           null comment 'User Nickname',
    userAvatar   varchar(1024)                          null comment 'User Avatar',
    userProfile  varchar(512)                           null comment 'User Profile',
    userRole     varchar(256) default 'user'            not null comment 'User Role: user/admin/ban',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment 'Edit Time',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    isDelete     tinyint      default 0                 not null comment 'Deleted Flag',
    index idx_unionId (unionId)
) comment 'User Table' collate = utf8mb4_unicode_ci;

-- Question Bank Table
create table if not exists question_bank
(
    id          bigint auto_increment comment 'ID' primary key,
    title       varchar(256)                       null comment 'Title',
    description text                               null comment 'Description',
    picture     varchar(2048)                      null comment 'Picture',
    userId      bigint                             not null comment 'Creator User ID',
    editTime    datetime default CURRENT_TIMESTAMP not null comment 'Edit Time',
    createTime  datetime default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    isDelete    tinyint  default 0                 not null comment 'Deleted Flag',
    index idx_title (title)
) comment 'Question Bank Table' collate = utf8mb4_unicode_ci;

-- Question Table
create table if not exists question
(
    id         bigint auto_increment comment 'ID' primary key,
    title      varchar(256)                       null comment 'Title',
    content    text                               null comment 'Content',
    tags       varchar(1024)                      null comment 'Tag List (JSON Array)',
    answer     text                               null comment 'Recommended Answer',
    userId     bigint                             not null comment 'Creator User ID',
    editTime   datetime default CURRENT_TIMESTAMP not null comment 'Edit Time',
    createTime datetime default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    isDelete   tinyint  default 0                 not null comment 'Deleted Flag',
    index idx_title (title),
    index idx_userId (userId)
) comment 'Question Table' collate = utf8mb4_unicode_ci;

-- Question Bank Question Table (Hard Delete)
create table if not exists question_bank_question
(
    id             bigint auto_increment comment 'ID' primary key,
    questionBankId bigint                             not null comment 'Question Bank ID',
    questionId     bigint                             not null comment 'Question ID',
    userId         bigint                             not null comment 'Creator User ID',
    createTime     datetime default CURRENT_TIMESTAMP not null comment 'Creation Time',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'Update Time',
    UNIQUE (questionBankId, questionId)
) comment 'Question Bank Question Table' collate = utf8mb4_unicode_ci;

ALTER TABLE user
    ADD phoneNumber VARCHAR(20) COMMENT 'phone number',
    ADD email VARCHAR(256) COMMENT 'email',
    ADD grade VARCHAR(50) COMMENT ' graduation time',
    ADD workExperience VARCHAR(512) COMMENT 'work experience',
    ADD expertiseDirection VARCHAR(512) COMMENT 'expertise';
