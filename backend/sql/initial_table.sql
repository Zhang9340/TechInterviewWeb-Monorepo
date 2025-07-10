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

use  techInterviewWeb;
-- Initial Data for User Table (Passwords are "12345678")
INSERT INTO user (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole)
VALUES
    (1, 'user1', 'b0dd3697a192885d7c055db46155b26a', 'unionId1', 'mpOpenId1', 'user1',
     'https://www.code-nav.cn/logo.png', 'A programming enthusiast', 'user'),
    (2, 'user2', 'b0dd3697a192885d7c055db46155b26a', 'unionId2', 'mpOpenId2', 'user2',
     'https://www.code-nav.cn/logo.png', 'Full-stack developer', 'user'),
    (3, 'user3', 'b0dd3697a192885d7c055db46155b26a', 'unionId3', 'mpOpenId3', 'user3',
     'https://www.code-nav.cn/logo.png', 'Frontend enthusiast', 'user'),
    (4, 'user4', 'b0dd3697a192885d7c055db46155b26a', 'unionId4', 'mpOpenId4', 'user4',
     'https://www.code-nav.cn/logo.png', 'Backend developer', 'user'),
    (5, 'zzy123', 'b0dd3697a192885d7c055db46155b26a', NULL, NULL, 'zzy the Programmer',
     'https://www.code-nav.cn/logo.png', 'System administrator', 'admin');

-- Initial Data for Question Bank Table
INSERT INTO question_bank (title, description, picture, userId)
VALUES
    ('JavaScript Basics', 'Contains basic knowledge questions about JavaScript',
     'https://pic.code-nav.cn/mianshiya/question_bank_picture/1777886594896760834/JldkWf9w_JavaScript.png', 1),
    ('CSS Styles', 'Contains questions about CSS styles',
     'https://pic.code-nav.cn/mianshiya/question_bank_picture/1777886594896760834/QatnFmEN_CSS.png', 2),
    ('HTML Basics', 'Basic knowledge of HTML markup language',
     'https://www.mianshiya.com/logo.png', 3),
    ('Frontend Frameworks', 'Questions about frameworks such as React, Vue, and Angular',
     'https://www.mianshiya.com/logo.png', 1),
    ('Algorithms and Data Structures', 'Questions about data structures and algorithms',
     'https://www.mianshiya.com/logo.png', 2),
    ('Database Principles', 'SQL statements and database design',
     'https://www.mianshiya.com/logo.png', 3),
    ('Operating Systems', 'Basic concepts of operating systems',
     'https://www.mianshiya.com/logo.png', 1),
    ('Network Protocols', 'Questions about HTTP, TCP/IP, and other network protocols',
     'https://www.mianshiya.com/logo.png', 2),
    ('Design Patterns', 'Common design patterns and their applications',
     'https://www.mianshiya.com/logo.png', 3),
    ('Programming Language Overview', 'Basic knowledge of multiple programming languages',
     'https://www.mianshiya.com/logo.png', 1),
    ('Version Control', 'Using Git and SVN',
     'https://www.mianshiya.com/logo.png', 2),
    ('Security and Encryption', 'Network security and encryption technologies',
     'https://www.mianshiya.com/logo.png', 3),
    ('Cloud Computing', 'Cloud services and architecture',
     'https://www.mianshiya.com/logo.png', 1),
    ('Microservices Architecture', 'Design and implementation of microservices',
     'https://www.mianshiya.com/logo.png', 2),
    ('Container Technology', 'Knowledge about Docker and Kubernetes',
     'https://www.mianshiya.com/logo.png', 3),
    ('DevOps Practices', 'Continuous Integration and Continuous Delivery',
     'https://www.mianshiya.com/logo.png', 1),
    ('Data Analysis', 'Data analysis and visualization',
     'https://www.mianshiya.com/logo.png', 2),
    ('Artificial Intelligence', 'Basics of machine learning and deep learning',
     'https://www.mianshiya.com/logo.png', 3),
    ('Blockchain Technology', 'Basic principles and applications of blockchain',
     'https://www.mianshiya.com/logo.png', 1),
    ('Project Management', 'Management and execution of software development projects',
     'https://www.mianshiya.com/logo.png', 2);

-- Initial Data for Question Table
INSERT INTO question (title, content, tags, answer, userId)
VALUES
    ('JavaScript Variable Hoisting', 'Explain the concept of variable hoisting in JavaScript.',
     '["JavaScript", "Basics"]',
     'Variable hoisting means that variable declarations are moved to the top of their scope in JavaScript.', 1),
    ('CSS Flexbox Layout', 'How can you center a box horizontally using CSS?',
     '["CSS", "Layout"]',
     'You can use the Flexbox layout by setting the parent container\'s display to flex and using justify-content: center.', 2),
('HTML Semantics', 'What is HTML semantics and why is it important?',
    '["HTML", "Semantics"]',
    'HTML semantics involves using the correct tags to describe the meaning of content. It improves accessibility and SEO.', 3),
('State Management in React', 'How can you manage state in React components?',
    '["React", "State Management"]',
    'You can manage state using React\'s useState or useReducer hooks, or by using a global state management library like Redux.', 1),
    ('Algorithm: Binary Search', 'Implement a binary search algorithm.',
     '["Algorithm", "Data Structures"]',
     'Binary search is an algorithm that finds a target in a sorted array by repeatedly dividing the search range in half.', 2),
    ('Database Indexes', 'What is a database index and what is its purpose?',
     '["Database", "Indexes"]',
     'A database index is a data structure used to speed up query performance by optimizing lookup paths.', 3),
    ('HTTP vs HTTPS', 'Explain the primary differences between HTTP and HTTPS.',
     '["Networking", "Protocols"]',
     'HTTPS is the encrypted version of HTTP, providing secure data transmission through SSL/TLS.', 1),
    ('Design Patterns: Singleton', 'Explain the Singleton pattern and its use cases.',
     '["Design Patterns", "Singleton"]',
     'The Singleton pattern ensures a class has only one instance and provides a global access point. Common in configuration classes.', 2),
    ('Git Branch Management', 'How do you manage branches in Git?',
     '["Version Control", "Git"]',
     'Branches are managed using Git\'s branch command to create branches, checkout to switch, and merge to combine.', 3),
('Docker Basics', 'List and explain some common Docker commands.',
    '["Container Technology", "Docker"]',
    'Common commands include docker run, docker build, docker ps, and docker stop.', 1);

INSERT INTO question_bank_question (questionBankId, questionId, userId)
VALUES
(1, 1, 1),
(1, 2, 1),
(1, 3, 1),
(1, 4, 1),
(1, 5, 1),
(1, 6, 1),
(1, 7, 1),
(1, 8, 1),
(1, 9, 1),
(1, 10, 1),
(2, 2, 2),
(2, 14, 2),
(3, 3, 3),
(3, 13, 3),
(4, 4, 1),
(4, 16, 1),
(5, 5, 2),
(5, 18, 2),
(6, 6, 3),
(6, 19, 3),
(7, 7, 1),
(7, 11, 1),
(8, 8, 2),
(8, 10, 2),
(9, 9, 3),
(9, 17, 3),
(10, 12, 1),
(10, 20, 1);
