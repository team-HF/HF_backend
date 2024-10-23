CREATE DATABASE IF NOT EXISTS hf;

USE hf;

DROP PROCEDURE IF EXISTS drop_all_tables;

SET FOREIGN_KEY_CHECKS = 0;

DELIMITER
$$
CREATE PROCEDURE drop_all_tables()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE tname VARCHAR(64);

    DECLARE table_cur CURSOR FOR SELECT
                                     table_name
                                 FROM information_schema.tables
                                 WHERE table_schema = 'hf';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    OPEN table_cur;

    table_loop
    :
    LOOP
        FETCH table_cur INTO tname;
        IF done THEN LEAVE table_loop; END IF;
        SET @tname_schema = CONCAT('hf.', tname);
        SET @drop_table_sql = CONCAT('DROP TABLE IF EXISTS ', @tname_schema);
        PREPARE stmt FROM @drop_table_sql;
        EXECUTE stmt;
    END LOOP;

    CLOSE table_cur;
END;
$$

DELIMITER ;

CALL drop_all_tables();

SET FOREIGN_KEY_CHECKS = 1;

-- DB 스키마 정의 시작
-- 추후 ERD 작성 후 필요한 테이블 다 추가할 예정
-- 각 길이는 논의 후에 구체적으로 정한다 (여기 DDL 숫자만 바꾸면 돼서 추후에 쉽게 변경할 수 있을 듯)

CREATE TABLE members
(
    member_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    login_id          VARCHAR(40) UNIQUE                 NOT NULL, -- 정책 확인 후 VARCHAR 크기 재조정
    password          VARCHAR(40),                                 -- 소셜 로그인 시 NULL
    name              VARCHAR(10)                        NOT NULL,
    email             VARCHAR(40) UNIQUE                 NOT NULL, -- 소셜 로그인 시 member_id와 email 같음
    role              ENUM ('ROLE_MEMBER', 'ROLE_ADMIN') DEFAULT 'ROLE_MEMBER',
    creation_time     DATETIME                           DEFAULT NOW(),
    nickname          VARCHAR(20)                        NOT NULL, -- 닉네임 최대 8자인데 2자 에누리
    profile_url       VARCHAR(255),
    cd1               CHAR(2)                            NOT NULL,
    cd2               CHAR(3)                            NOT NULL,
    cd3               CHAR(3)                            NOT NULL,
    birth_date        DATE                               NOT NULL,
    gender            ENUM ('MALE', 'FEMALE')            NOT NULL, -- 추후 정책에 따라 Gender 추가 가능
    introduction      TEXT                               NOT NULL, -- 추후 재조정
    fitness_level     ENUM ('ADVANCED', 'BEGINNER')      NOT NULL,
    companion_style   ENUM ('SMALL', 'GROUP')            NOT NULL,
    fitness_eagerness ENUM ('EAGER', 'LAZY')             NOT NULL,
    fitness_objective ENUM ('BULK_UP', 'RUNNING')        NOT NULL, -- 추후 ENUM 값 수정
    fitness_kind      ENUM ('HIGH_STRESS', 'FUNCTIONAL') NOT NULL, -- 추후 ENUM 값 수정
    review_score      DOUBLE                             DEFAULT 0.0,
    is_deleted        BOOLEAN                            DEFAULT FALSE
);

CREATE TABLE matching
(
    matching_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    requester_id      BIGINT   NOT NULL,
    request_target_id BIGINT   NOT NULL,
    status            ENUM ('PENDING', 'ACCEPTED', 'REJECTED', 'FINISHED') DEFAULT 'PENDING',
    meeting_time      DATETIME NOT NULL,
    creation_time     DATETIME                                             DEFAULT NOW(),
    finish_time       DATETIME,
    FOREIGN KEY (requester_id) REFERENCES members (member_id),
    FOREIGN KEY (request_target_id) REFERENCES members (member_id)
);

CREATE TABLE review
(
    review_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    reviewer_id   BIGINT  NOT NULL,
    reviewee_id   BIGINT  NOT NULL,
    matching_id   BIGINT  NOT NULL,
    creation_time DATETIME DEFAULT NOW(),
    last_modified DATETIME,
    score         INTEGER NOT NULL,
    FOREIGN KEY (reviewer_id) REFERENCES members (member_id),
    FOREIGN KEY (matching_id) REFERENCES matching (matching_id),
    CONSTRAINT score_range CHECK (score >= 1 AND score <= 5)
);

CREATE TABLE review_evaluation
(
    review_evaluation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    review_id            BIGINT                    NOT NULL,
    evaluation_type      ENUM ('GOOD', 'NOT_GOOD') NOT NULL,
    evaluation_detail_id INTEGER                   NOT NULL,
    FOREIGN KEY (review_id) REFERENCES review (review_id)
);

CREATE TABLE spec
(
    spec_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id   BIGINT       NOT NULL,
    start_date  DATE         NOT NULL,
    end_date    DATE,
    is_current  BOOLEAN      NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (member_id) REFERENCES members (member_id)
);

CREATE TABLE post
(
    post_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    category      ENUM ('GYM_RECOMMENDATION', 'WORKOUT_CERTIFICATION', 'COUNSELING') NOT NULL,
    writer_id     BIGINT                                                             NOT NULL,
    title         VARCHAR(50)                                                        NOT NULL,
    content       TEXT                                                               NOT NULL,
    creation_time DATETIME DEFAULT NOW(),
    last_modified DATETIME,
    likes_count   BIGINT   DEFAULT 0,
    view_count    BIGINT   DEFAULT 0,
    is_deleted    BOOLEAN  DEFAULT FALSE,
    FOREIGN KEY (writer_id) REFERENCES members (member_id)
);

CREATE TABLE comment
(
    comment_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id       BIGINT NOT NULL,
    writer_id     BIGINT NOT NULL,
    content       TEXT   NOT NULL,
    creation_time DATETIME DEFAULT NOW(),
    last_modified DATETIME,
    is_deleted    BOOLEAN  DEFAULT FALSE,
    FOREIGN KEY (post_id) REFERENCES post (post_id),
    FOREIGN KEY (writer_id) REFERENCES members (member_id)
);

CREATE TABLE likes
(
    like_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id   BIGINT                   NOT NULL,
    post_id     BIGINT,
    comment_id  BIGINT,
    like_type   ENUM ('POST', 'COMMENT') NOT NULL,
    is_canceled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (member_id) REFERENCES members (member_id),
    FOREIGN KEY (post_id) REFERENCES post (post_id),
    FOREIGN KEY (comment_id) REFERENCES comment (comment_id)
);

CREATE TABLE chatroom
(
    chatroom_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    chatroom_name VARCHAR(255) NOT NULL,
    creation_time DATETIME DEFAULT NOW(),
    thumbnail_url VARCHAR(255),
    is_deleted    BOOLEAN  DEFAULT FALSE
);

CREATE TABLE chat_participation
(
    chat_participation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chatroom_id           BIGINT NOT NULL,
    participant_id        BIGINT NOT NULL,
    is_disconnected       BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (chatroom_id) REFERENCES chatroom (chatroom_id),
    FOREIGN KEY (participant_id) REFERENCES members (member_id)
);

CREATE TABLE chat_message
(
    chat_message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chatroom_id     BIGINT NOT NULL,
    sender_id       BIGINT NOT NULL,
    content         TEXT   NOT NULL,
    creation_time   DATETIME DEFAULT NOW(),
    is_deleted      BOOLEAN  DEFAULT FALSE,
    FOREIGN KEY (chatroom_id) REFERENCES chatroom (chatroom_id),
    FOREIGN KEY (sender_id) REFERENCES members (member_id)
);

CREATE TABLE chat_read
(
    chat_message_id BIGINT NOT NULL,
    reader_id       BIGINT NOT NULL,
    PRIMARY KEY (chat_message_id, reader_id),
    FOREIGN KEY (chat_message_id) REFERENCES chat_message (chat_message_id),
    FOREIGN KEY (reader_id) REFERENCES members (member_id)
);

CREATE TABLE notification
(
    notification_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    receiver_id       BIGINT       NOT NULL,
    logo_image_url    VARCHAR(255),
    title             VARCHAR(255) NOT NULL,
    content           TEXT         NOT NULL,
    notification_time DATETIME DEFAULT NOW(),
    type              ENUM ('MATCHING'),
    is_checked        BOOLEAN  DEFAULT FALSE,
    is_deleted        BOOLEAN  DEFAULT FALSE,
    FOREIGN KEY (receiver_id) REFERENCES members (member_id)
);